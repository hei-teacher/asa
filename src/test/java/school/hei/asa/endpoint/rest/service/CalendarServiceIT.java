package school.hei.asa.endpoint.rest.service;

import static java.time.Month.DECEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.hei.asa.conf.EnvConf.DUMMY_CARE_PRODUCT_CODE;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.DailyExecution.Type.mixedWorkAndCare;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.DailyExecutionController;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.SecurityConfig;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.ContractType;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JContractLevel;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.service.CalendarService;

class CalendarServiceIT extends FacadeIT {
  @Autowired DailyExecutionController dailyExecutionController;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;

  @MockBean SecurityConfig securityConfig;
  @MockBean WorkerFromAuthentication workerFromAuthentication;

  Authentication authentication;
  RedirectAttributes redirectAttributes;
  String authenticatedWorkerCode = "worker-code";

  @Autowired CalendarService calendarService;
  @Autowired EntityManager entityManager;
  @Autowired TransactionTemplate transactionTemplate;

  @AfterEach
  void tearDown() {
    transactionTemplate.execute(
        status -> {
          entityManager
              .createQuery("delete from JContract where id = 'contract-calendar'")
              .executeUpdate();
          entityManager
              .createQuery("delete from JContractLevel where code = 'L-CALENDAR'")
              .executeUpdate();
          return null;
        });
  }

  @BeforeEach
  void setUp() {
    authentication = authentication();
    redirectAttributes = mock(RedirectAttributes.class);
    setUpProductsAndMissions();
    addContract();
  }

  private void addContract() {
    transactionTemplate.execute(
        status -> {
          var jContractLevel = new JContractLevel();
          jContractLevel.setCode("L-CALENDAR");
          jContractLevel.setType(ContractType.partnerContractor);
          jContractLevel.setDailyPay(100000.0);
          entityManager.persist(jContractLevel);

          var jWorker = entityManager.find(JWorker.class, authenticatedWorkerCode);
          var jContract = new JContract();
          jContract.setId("contract-calendar");
          jContract.setWorker(jWorker);
          jContract.setLevel(jContractLevel);
          jContract.setEntranceInstant(Instant.parse("2024-01-01T00:00:00Z"));
          jContract.setDurationInDays(365);
          jContract.setJobTitle("job_title");
          jContract.setContractBucketKey("contract_bucket_key");
          entityManager.persist(jContract);
          return null;
        });
  }

  @Test
  void datesByDailyExecution_by_fullWork() {
    dailyExecutionController.createDailyExecution(
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
        redirectAttributes);

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
    dailyExecutionController.createDailyExecution(
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
        redirectAttributes);
    dailyExecutionController.createDailyExecution(
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
        redirectAttributes);

    var worker = workerRepository.findByCode(authenticatedWorkerCode);
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, 2025);
    assertEquals(2, datesByDailyExecutionType.get(fullCare).size());
    assertEquals(0, datesByDailyExecutionType.get(fullWork).size());
    assertEquals(0, datesByDailyExecutionType.get(mixedWorkAndCare).size());
  }

  @Test
  void datesByDailyExecution_by_mixedWorkAndCare() {
    dailyExecutionController.createDailyExecution(
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
        redirectAttributes);

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
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    return authentication;
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
