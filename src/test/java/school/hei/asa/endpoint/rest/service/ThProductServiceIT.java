package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;

class ThProductServiceIT extends FacadeIT {
  @Autowired ThProductService thProductService;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;

  @MockBean WorkerFromAuthentication workerFromAuthentication;

  Authentication authentication;
  Worker authenticatedWorker;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    authenticatedWorker =
        new Worker(
            "workerCode",
            "workerName",
            "email",
            "fullname",
            "address",
            "random city",
            "nif",
            "stat");
    workerRepository.save(authenticatedWorker);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    var product = new Product("product-code", "product-name", "product-description");
    productRepository.save(product);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));
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

    var thProductsByMonth = thProductService.thProductsExecutedDaysSumByMonth(thProducts, true);

    var januaryExecutedDays = thProductsByMonth.get("january");
    var februaryExecutedDays = thProductsByMonth.get("february");
    assertEquals(0.5, januaryExecutedDays);
    assertEquals(1.5, februaryExecutedDays);
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

    var result = thProductService.thProductsByMonth(thProducts).get("february");

    assertEquals(0, result.getFirst().missions().getFirst().getMissionExecutions().size());
    assertEquals(1, result.getLast().missions().getFirst().getMissionExecutions().size());
  }

  @Test
  void filter_product_by_date_between() {
    var missionExecution1 =
        new ThMissionExecution(
            "mission0-code",
            "W-P-2024-01",
            LocalDate.parse("2024-07-01"),
            0.5,
            "comment3",
            false,
            true);
    var missionExecution2 =
        new ThMissionExecution(
            "mission0-code",
            "W-P-2024-01",
            LocalDate.parse("2024-07-01"),
            0.5,
            "comment4",
            false,
            true);
    var mission =
        new ThMission(
            "mission0-code",
            "a mission",
            "a description",
            List.of(missionExecution1, missionExecution2),
            false,
            false);
    var expected = new ThProduct("pCode0", "pname0", "pDescription0", List.of(mission), false);

    var actual =
        thProductService
            .filterThProductByWorkerCodeAndDateBetween(
                "W-P-2024-01", "2024-06-01", "2024-08-01", true)
            .stream()
            .filter(p -> p.code().equals("pCode0"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Test product not found"));

    assertEquals(expected, actual);
  }
}
