package school.hei.asa.service;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.DailyExecutionController;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.endpoint.rest.security.SecurityConfig;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;

@Transactional
class MissionServiceIT extends FacadeIT {

  @Autowired MissionService missionService;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;
  @Autowired DailyExecutionController dailyExecutionController;
  @Autowired WorkerRepository workerRepository;

  @MockBean SecurityConfig securityConfig;
  @MockBean WorkerFromAuthentication workerFromAuthentication;

  Authentication authentication;
  Worker authenticatedWorker;

  @BeforeEach
  void setUp() {
    var product = new Product("product-code", "product-name", "product-description");
    productRepository.save(product);
    var mission1 = new Mission("code1", "title1", "description1", 10, product);
    var mission2 = new Mission("code2", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));

    authentication = mock(Authentication.class);
    authenticatedWorker =
        new Worker(
            "workerCode", "code", "email", "full code", "address", "random city", "nif", "stat");
    workerRepository.save(authenticatedWorker);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
  }

  @Test
  void thProductsExecutedDays_count_by_month() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission("code1", "mission1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission("code2", "mission2", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission("code3", "mission3", "description", List.of(missionExecution3), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false));

    var thProducsByMonth = missionService.thProductsExecutedDaysSumByMonth(thProducts, true);

    var januaryExecutedDays = thProducsByMonth.get("january");
    var februaryExecutedDays = thProducsByMonth.get("february");
    assertEquals(0.5, januaryExecutedDays);
    assertEquals(1.5, februaryExecutedDays);
  }

  @Test
  void filter_ThMissions() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var thMissions = missionService.getUniqueMissionsByTitle(thProducts);

    assertEquals(4, thMissions.size());
  }

  @Test
  void filter_ThMissions_with_same_title() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var thMissions = missionService.getAllMissionsFromProducts(thProducts);

    assertEquals(2, thMissions.size());
  }

  @Test
  void fetch_product_from_database() {
    var thProducts =
        missionService.filterThProductByWorkerCodeAndDateBetween(null, null, null, true);

    assertFalse(thProducts.isEmpty());
  }

  @Test
  void group_product_by_month() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var result = missionService.thProductsByMonth(thProducts).get("february");

    assertEquals(0, result.getFirst().missions().getFirst().getMissionExecutions().size());
    assertEquals(1, result.getLast().missions().getFirst().getMissionExecutions().size());
  }

  @Test
  void filter_product_by_date_between() {
    var dmeForm =
        new ThDailyExecutionForm(
            "2024-07-01",
            "code1",
            "0.5",
            "missionComment1",
            "code1",
            "0.5",
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

    dailyExecutionController.createDailyExecution(authentication, dmeForm);

    var savedWorker = workerRepository.findByCode(authenticatedWorker.code());
    var expected =
        new ThProduct("product-code", "product-name", "product-description", List.of(), false);

    var thProducts =
        missionService
            .filterThProductByWorkerCodeAndDateBetween(
                savedWorker.code(), "2024-06-01", "2025-06-01", true)
            .stream()
            .filter(p -> p.code().equals("product-code")) // Filter for your test data
            .findFirst()
            .orElseThrow(() -> new AssertionError("Test product not found"));

    assertEquals(expected, thProducts);
  }

  @Test
  @SneakyThrows
  void export_contract_for_one_worker() {
    var actualCSV = missionService.generateCSV("W-P-2024-01");

    var actualContent = Files.readString(actualCSV.toPath());

    var expectedCSV = expectedFile();
    var expectedContent = Files.readString(expectedCSV.toPath());

    assertEquals(expectedContent, actualContent);
  }

  private File expectedFile() {
    String filePath = System.getProperty("java.io.tmpdir");
    File file = new File(filePath, "test.csv");
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(
          "code,worker,contract level,start date,"
              + "contract duration (in days),"
              + "total days worked,remaining days"
              + lineSeparator());
      fileWriter.flush();
      fileWriter.write(
          String.format("W-P-2024-01,Lita Andria,L4P-2026,2025-01-01,80,-,-" + lineSeparator()));
      fileWriter.write(
          String.format("W-P-2024-01,Lita Andria,L5,2023-01-01,13,2.0,11.0" + lineSeparator()));
      fileWriter.flush();
      return file;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
