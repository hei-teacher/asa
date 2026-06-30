package school.hei.asa.endpoint.rest.controller;

import static java.time.Month.DECEMBER;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.SecurityConfig;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.MissionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.service.ContractService;

class DailyExecutionControllerIT extends FacadeIT {

  @Autowired DailyExecutionController dailyExecutionController;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;
  @Autowired DailyExecutionRepository dailyExecutionRepository;
  @Autowired CalendarController calendarController;
  @Autowired JdbcTemplate jdbcTemplate;
  @Autowired ContractService contractService;
  @MockBean SecurityConfig securityConfig;
  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean Mailer mailer;

  Authentication authentication;
  Worker authenticatedWorker;
  Model model;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    authenticatedWorker =
        new Worker(
            "worker-code", "code", "email", "full code", "address", "random city", "nif", "stat");
    workerRepository.save(authenticatedWorker);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    var product = new Product("pcode", "pname", "pdescription");
    productRepository.save(product);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));

    jdbcTemplate.update(
        "INSERT INTO contract_level (code, type, daily_pay) VALUES (?, ?, ?) "
            + "ON CONFLICT DO NOTHING",
        "L-TEST", "studentContractor", 25000.0);
    jdbcTemplate.update(
        "INSERT INTO contract (id, worker_code, level, entrance_instant, duration_in_days) "
            + "VALUES (?, ?, ?, ?::timestamp, ?) ON CONFLICT DO NOTHING",
        "contract-daily-exec", authenticatedWorker.code(), "L-TEST",
        "2025-01-01 00:00:00", 80);

    model = mock(Model.class);
  }

  @Test
  void save_then_read_with_duplicates_ok_if_sum_of_set_is_100() {
    setUp();
    var dmeForm =
        new ThDailyExecutionForm(
            "2024-12-03",
            "mission1-code",
            "0.4",
            "missionComment1",
            "mission2-code",
            "0.6",
            "missionComment2",
            // duplicate of mission2 (missionCode2, missionPercentage2, missionComment2)
            "mission2-code",
            "0.6",
            "missionComment2",
            null,
            null,
            null,
            null,
            null,
            null);

    dailyExecutionController.createDailyExecution(authentication, dmeForm, new RedirectAttributesModelMap());

    var savedWorker = workerRepository.findByCode(authenticatedWorker.code());
    var dailyExecutions =
        dailyExecutionRepository.findByWorkerCodeAndDateBetween(
            savedWorker.code(), LocalDate.of(2024, DECEMBER, 3), LocalDate.of(2024, DECEMBER, 3));
    assertEquals(1, dailyExecutions.size());
    var savedMission1 = missionRepository.findByCode("mission1-code");
    assertEquals(1, savedMission1.get().workers().size());
    var savedDailyExecutions =
        dailyExecutionRepository.findAll().stream()
            .filter(de -> savedWorker.equals(de.worker()))
            .toList();
    assertEquals(1, savedDailyExecutions.size());
    var savedProduct = productRepository.findByCode("pcode");
    assertEquals(1, savedProduct.executedDays(), 0);
  }

  @Test
  void cannot_save_if_mission_execution_already_exists() {
    setUp();
    var dmeForm =
        new ThDailyExecutionForm(
            "2024-12-01",
            "mission1-code",
            "0.2",
            "missionComment1",
            "mission2-code",
            "0.8",
            "missionComment2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    dailyExecutionController.createDailyExecution(authentication, dmeForm, new RedirectAttributesModelMap());
    assertThrows(
        Exception.class,
        () -> dailyExecutionController.createDailyExecution(authentication, dmeForm, new RedirectAttributesModelMap()));
  }

  @Test
  void read_worker_lita_with_duplicate_missions_and_percentage_over_100_ok() {
    LocalDate firstOfJanuary2024 = LocalDate.parse("2024-01-01");
    dailyExecutionRepository.findByWorkerCodeAndDateBetween(
        "W-P-2024-01", firstOfJanuary2024, firstOfJanuary2024);
  }

  @Test
  void concurrently_create_daily_execution() {
    setUp();
    var dmeForm =
        new ThDailyExecutionForm(
            "2024-12-01",
            "mission1-code",
            "0.2",
            "missionComment1",
            "mission2-code",
            "0.8",
            "missionComment2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var concurrentCalls = 1000;
    var executor = newFixedThreadPool(10);
    var latch = new CountDownLatch(1);
    var futures = new ArrayList<Future<String>>();
    for (int i = 0; i < concurrentCalls; i++) {
      futures.add(
          executor.submit(
              () -> {
                try {
                  latch.await();
                  return dailyExecutionController.createDailyExecution(authentication, dmeForm, new RedirectAttributesModelMap());
                } catch (Exception e) {
                  return e.getMessage();
                }
              }));
    }

    latch.countDown();
    var responses = futures.stream().map(this::getFutureResult).toList();

    long successCount =
        responses.stream()
            .filter(response -> response.contains("redirect:/work-and-care-calendar"))
            .count();
    assertEquals(1, successCount);

    executor.shutdown();
  }

  @Test
  void can_get_daily_execution_form() {
    setUp();
    var viewName = dailyExecutionController.getDailyExecutionForm(model);

    verify(model).addAttribute(eq("missions"), any(List.class));

    assertEquals("daily-execution", viewName);
  }

  @Test
  void cannot_save_if_contract_has_no_remaining_days() {
    var productExhausted = new Product("pcode-exhausted", "pname-ex", "pdesc-ex");
    productRepository.save(productExhausted);
    var missionExhausted = new Mission("mission-ex-code", "title-ex", "desc-ex", 10, productExhausted);
    missionRepository.save(missionExhausted);

    var workerNoDays =
        new Worker(
            "worker-no-days", "nodays", "nodays@test.com", "No Days", "", "", "", "");
    workerRepository.save(workerNoDays);

    jdbcTemplate.update(
        "INSERT INTO contract_level (code, type, daily_pay) VALUES (?, ?, ?) "
            + "ON CONFLICT DO NOTHING",
        "L-TEST-EXHAUSTED", "studentContractor", 25000.0);
    jdbcTemplate.update(
        "INSERT INTO contract (id, worker_code, level, entrance_instant, duration_in_days) "
            + "VALUES (?, ?, ?, ?::timestamp, ?) ON CONFLICT DO NOTHING",
        "contract-exhausted", workerNoDays.code(), "L-TEST-EXHAUSTED",
        "2025-01-01 00:00:00", 1);

    jdbcTemplate.update(
        "INSERT INTO mission_execution (id, mission_code, worker_code, date, day_percentage, "
            + "creation_instant, comment) VALUES (?, ?, ?, ?::date, ?, ?::timestamptz, ?)",
        "me-exhausted", "mission-ex-code", workerNoDays.code(), "2025-01-01", 1.0,
        Timestamp.valueOf("2025-01-01 12:00:00"), "already used day");

    var hasRemaining = contractService.hasRemainingDays(workerNoDays);
    assertEquals(false, hasRemaining);
  }

  @Test
  void can_save_if_contract_has_remaining_days() {
    var productRemaining = new Product("pcode-remaining", "pname-rem", "pdesc-rem");
    productRepository.save(productRemaining);
    var missionRemaining = new Mission("mission-rem-code", "title-rem", "desc-rem", 10, productRemaining);
    missionRepository.save(missionRemaining);

    var workerRemaining =
        new Worker(
            "worker-remaining", "remaining", "remaining@test.com", "Remaining", "", "", "", "");
    workerRepository.save(workerRemaining);

    jdbcTemplate.update(
        "INSERT INTO contract_level (code, type, daily_pay) VALUES (?, ?, ?) "
            + "ON CONFLICT DO NOTHING",
        "L-TEST-REMAINING", "studentContractor", 25000.0);
    jdbcTemplate.update(
        "INSERT INTO contract (id, worker_code, level, entrance_instant, duration_in_days) "
            + "VALUES (?, ?, ?, ?::timestamp, ?) ON CONFLICT DO NOTHING",
        "contract-remaining", workerRemaining.code(), "L-TEST-REMAINING",
        "2025-01-01 00:00:00", 80);

    var hasRemaining = contractService.hasRemainingDays(workerRemaining);
    assertEquals(true, hasRemaining);
  }

  @Test
  void createDailyExecution_sends_alert_when_remaining_below_threshold() {
    var productAlert = new Product("pcode-alert", "pname-alert", "pdesc-alert");
    productRepository.save(productAlert);
    var missionAlert = new Mission("mission-alert-code", "title-alert", "desc-alert", 10, productAlert);
    missionRepository.save(missionAlert);

    var workerAlert = new Worker(
        "worker-alert", "alert", "alert@test.com", "Alert Worker", "", "", "", "");
    workerRepository.save(workerAlert);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(workerAlert));

    jdbcTemplate.update(
        "INSERT INTO contract_level (code, type, daily_pay) VALUES (?, ?, ?) "
            + "ON CONFLICT DO NOTHING",
        "L-TEST-ALERT", "studentContractor", 25000.0);
    jdbcTemplate.update(
        "INSERT INTO contract (id, worker_code, level, entrance_instant, duration_in_days) "
            + "VALUES (?, ?, ?, ?::timestamp, ?) ON CONFLICT DO NOTHING",
        "contract-alert", workerAlert.code(), "L-TEST-ALERT",
        "2025-01-01 00:00:00", 10);
    jdbcTemplate.update(
        "INSERT INTO mission_execution (id, mission_code, worker_code, date, day_percentage, "
            + "creation_instant, comment) VALUES (?, ?, ?, ?::date, ?, ?::timestamptz, ?)",
        "me-used-day", "mission-alert-code", workerAlert.code(), "2025-01-01", 1.0,
        Timestamp.valueOf("2025-01-01 12:00:00"), "first day");

    var dmeForm = new ThDailyExecutionForm(
        "2025-01-02", "mission-alert-code", "1.0", "alert comment",
        null, null, null, null, null, null, null, null, null, null, null, null);

    var redirectAttrs = new RedirectAttributesModelMap();
    dailyExecutionController.createDailyExecution(authentication, dmeForm, redirectAttrs);

    var flashAlert = redirectAttrs.getFlashAttributes().get("contractAlert");
    assertEquals("Attention : il ne reste que 8 jours sur votre contrat.", flashAlert);
  }

  private String getFutureResult(Future<String> future) {
    try {
      return future.get();
    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
