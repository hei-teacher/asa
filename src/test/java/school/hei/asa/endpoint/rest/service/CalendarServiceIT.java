package school.hei.asa.endpoint.rest.service;

import static java.time.Month.DECEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.hei.asa.conf.EnvConf.DUMMY_CARE_PRODUCT_CODE;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.DailyExecution.Type.mixedWorkAndCare;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.DailyExecutionController;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.SecurityConfig;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.repository.jrepository.JContractRepository;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JContractLevel;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.service.CalendarService;

class CalendarServiceIT extends FacadeIT {
  @Autowired DailyExecutionController dailyExecutionController;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;
  @Autowired JContractRepository jContractRepository;

  @MockBean SecurityConfig securityConfig;
  @MockBean WorkerFromAuthentication workerFromAuthentication;

  Authentication authentication;
  String authenticatedWorkerCode = "worker-code";

  @Autowired CalendarService calendarService;

  @BeforeEach
  void setUp() {
    authentication = authentication();
    setUpProductsAndMissions();
  }

  @Test
  void datesByDailyExecution_by_fullWork() {
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
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
            null),
        new RedirectAttributesModelMap());

    var worker = workerRepository.findByCode(authenticatedWorkerCode);
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, 2024);

    var fullWorkDates = datesByDailyExecutionType.get(fullWork);
    assertEquals(1, fullWorkDates.size());
    assertEquals(LocalDate.of(2024, DECEMBER, 1), fullWorkDates.get(0));
    assertEquals(0, datesByDailyExecutionType.get(fullCare).size());
    assertEquals(0, datesByDailyExecutionType.get(mixedWorkAndCare).size());
  }

  @Test
  void datesByDailyExecution_by_fullCare() {
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
        new ThDailyExecutionForm(
            "2025-12-01",
            "careMission-code",
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
            null),
        new RedirectAttributesModelMap());
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
        new ThDailyExecutionForm(
            "2025-10-01",
            "careMission-code",
            "1",
            "missionComment2",
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
            null),
        new RedirectAttributesModelMap());

    var worker = workerRepository.findByCode(authenticatedWorkerCode);
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, 2025);
    assertEquals(2, datesByDailyExecutionType.get(fullCare).size());
    assertEquals(0, datesByDailyExecutionType.get(fullWork).size());
    assertEquals(0, datesByDailyExecutionType.get(mixedWorkAndCare).size());
  }

  @Test
  void datesByDailyExecution_by_mixedWorkAndCare() {
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
        new ThDailyExecutionForm(
            "2024-06-01",
            "mission1-code",
            "0.2",
            "missionComment1",
            "careMission-code",
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
            null),
        new RedirectAttributesModelMap());

    var worker = workerRepository.findByCode(authenticatedWorkerCode);
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, 2024);

    assertEquals(0, datesByDailyExecutionType.get(fullCare).size());
    assertEquals(0, datesByDailyExecutionType.get(fullWork).size());
    assertEquals(1, datesByDailyExecutionType.get(mixedWorkAndCare).size());
  }

  private Authentication authentication() {
    var authentication = mock(Authentication.class);
    var authenticatedWorker =
        new Worker(
            authenticatedWorkerCode,
            "code",
            "email",
            "full code",
            "address",
            "random city",
            "nif",
            "stat");
    workerRepository.save(authenticatedWorker);
    saveActiveContractFor(authenticatedWorker);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    return authentication;
  }

  private void saveActiveContractFor(Worker worker) {
    var jWorker = new JWorker();
    jWorker.setCode(worker.code());
    var level = new JContractLevel();
    level.setCode("L4P-2026");
    var jContract = new JContract();
    jContract.setId(UUID.randomUUID().toString());
    jContract.setWorker(jWorker);
    jContract.setLevel(level);
    jContract.setEntranceInstant(Instant.parse("2020-01-01T00:00:00Z"));
    jContract.setEndInstant(null);
    jContract.setDurationInDays(365);
    jContract.setJobTitle("Test Job");
    jContract.setCompany("Test Company");
    jContract.setContractBucketKey("test-key");
    jContractRepository.save(jContract);
  }

  private void setUpProductsAndMissions() {
    var product = new Product("pcode", "pname", "pdescription");
    var careProduct = new Product(DUMMY_CARE_PRODUCT_CODE, "", "");
    productRepository.save(product);
    productRepository.save(careProduct);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    var careMission = new Mission("careMission-code", "", "", 2, careProduct);
    missionRepository.saveAll(List.of(mission1, mission2, careMission));
  }
}
