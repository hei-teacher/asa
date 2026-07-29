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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.ContractAlertRequested;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.SecurityConfig;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;

class DailyExecutionControllerIT extends FacadeIT {

  @Autowired DailyExecutionController dailyExecutionController;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;
  @Autowired DailyExecutionRepository dailyExecutionRepository;
  @Autowired CalendarController calendarController;

  @MockBean SecurityConfig securityConfig;
  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean EventProducer<ContractAlertRequested> eventProducer;

  Authentication authentication;
  Worker authenticatedWorker;
  Model model;
  RedirectAttributes redirectAttributes;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    redirectAttributes = mock(RedirectAttributes.class);
    authenticatedWorker =
        new Worker(
            "worker-code", "code", "email", "full code", "address", "random city", "nif", "stat");
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    var product = new Product("pcode", "pname", "pdescription");
    productRepository.save(product);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));
    model = mock(Model.class);
  }

  @Test
  void save_then_read_with_duplicates_ok_if_sum_of_set_is_100() {
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

    dailyExecutionController.createDailyExecution(authentication, dmeForm, redirectAttributes);

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

    dailyExecutionController.createDailyExecution(authentication, dmeForm, redirectAttributes);
    assertThrows(
        Exception.class,
        () ->
            dailyExecutionController.createDailyExecution(
                authentication, dmeForm, redirectAttributes));
  }

  @Test
  void read_worker_lita_with_duplicate_missions_and_percentage_over_100_ok() {
    LocalDate firstOfJanuary2024 = LocalDate.parse("2024-01-01");
    dailyExecutionRepository.findByWorkerCodeAndDateBetween(
        "W-P-2024-01", firstOfJanuary2024, firstOfJanuary2024);
  }

  @Test
  void concurrently_create_daily_execution() {
    var dmeForm =
        new ThDailyExecutionForm(
            "2024-12-02",
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
                  return dailyExecutionController.createDailyExecution(
                      authentication, dmeForm, redirectAttributes);
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
  void createDailyExecution_throws_when_contract_exhausted() {
    var exhaustedWorker = workerRepository.findByCode("exhausted-test-worker");
    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(exhaustedWorker));

    var dmeForm =
        new ThDailyExecutionForm(
            "2024-12-03",
            "mission1-code",
            "1",
            "missionComment1",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    var result =
        dailyExecutionController.createDailyExecution(authentication, dmeForm, redirectAttributes);
    assertEquals("redirect:/daily-execution", result);
    verify(redirectAttributes).addFlashAttribute(eq("error"), any(String.class));
  }

  @Test
  void createDailyExecution_calls_eventProducer_when_below_threshold() {
    var alertWorker = workerRepository.findByCode("alert-test-worker");
    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(alertWorker));

    var dmeForm =
        new ThDailyExecutionForm(
            "2024-12-03",
            "mission1-code",
            "1",
            "missionComment1",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    dailyExecutionController.createDailyExecution(authentication, dmeForm, redirectAttributes);
    verify(eventProducer).accept(any());
  }

  @Test
  void can_get_daily_execution_form() {
    var viewName = dailyExecutionController.getDailyExecutionForm(authentication, model);

    verify(model).addAttribute(eq("missions"), any(List.class));

    assertEquals("daily-execution", viewName);
  }

  private String getFutureResult(Future<String> future) {
    try {
      return future.get();
    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
