package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionExecutionMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.ProductService;

class ThProductServiceTest {

  private final ContractService contractService = mock(ContractService.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);

  private ThProductService createServiceWithRealMappers() {
    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var missionMapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);
    var productMapper = new ThProductMapper(missionMapper, careProductCodeSupplier);
    var productService = mock(ProductService.class);
    when(productService.getAllProducts()).thenReturn(List.of());
    var missionService = mock(school.hei.asa.service.MissionService.class);
    var thMissionService = new ThMissionService(missionService, missionMapper);
    return new ThProductService(productService, productMapper, thMissionService);
  }

  private ThProductService createServiceWithProducts(List<Product> products) {
    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var missionMapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);
    var productMapper = new ThProductMapper(missionMapper, careProductCodeSupplier);
    var productService = mock(ProductService.class);
    when(productService.getAllProducts()).thenReturn(products);
    var missionService = mock(school.hei.asa.service.MissionService.class);
    var thMissionService = new ThMissionService(missionService, missionMapper);
    return new ThProductService(productService, productMapper, thMissionService);
  }

  @Test
  void thProductsByMonth() {
    var me =
        new ThMissionExecution(
            "M1", "W1", java.time.LocalDate.of(2025, 1, 15), 1.0, "c1", false, false);
    var mission = new ThMission("M1", "Title1", "Desc", List.of(me), false, false);
    var product = new ThProduct("P1", "P1", "Desc", List.of(mission), false);

    var result = createServiceWithRealMappers().thProductsByMonth(List.of(product));

    assertTrue(result.containsKey("january"));
  }

  @Test
  void toProductChartData() {
    var product = new ThProduct("P1", "Product1", "Desc", List.of(), false);

    var result = createServiceWithRealMappers().toProductChartData(List.of(product));

    assertEquals(1, result.size());
    assertEquals("P1", result.getFirst().get("code"));
    assertEquals("Product1", result.getFirst().get("name"));
  }

  @Test
  void thProductsExecutedDaysSum() {
    var me =
        new ThMissionExecution(
            "M1", "W1", java.time.LocalDate.of(2025, 1, 15), 0.5, "c1", false, false);
    var mission = new ThMission("M1", "Title1", "Desc", List.of(me), false, false);
    var product = new ThProduct("P1", "P1", "Desc", List.of(mission), false);

    var result =
        createServiceWithRealMappers()
            .thProductsExecutedDaysSum(List.of(product), Month.JANUARY, false);

    assertEquals(0.5, result);
  }

  @Test
  void filterThProductByWorkerCodeAndDateBetween_withNullDates() {
    var worker = new Worker("W1", "name", "email", "fn", "addr", "city", "nif", "stat");
    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M1", "M1", "Desc", 10, product);
    var now = LocalDate.now();
    mission.add(new MissionExecution(mission, worker, now, 1.0, "c1", Instant.now()));
    var service = createServiceWithProducts(List.of(product));

    var result = service.filterThProductByWorkerCodeAndDateBetween("W1", null, null, false);

    assertEquals(1, result.size());
    assertEquals("P1", result.getFirst().code());
  }

  @Test
  void filterThProductByWorkerCodeAndDateBetween_withValidDates() {
    var worker = new Worker("W1", "name", "email", "fn", "addr", "city", "nif", "stat");
    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M1", "M1", "Desc", 10, product);
    mission.add(
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 15), 1.0, "c1", Instant.now()));
    var service = createServiceWithProducts(List.of(product));

    var result =
        service.filterThProductByWorkerCodeAndDateBetween("W1", "2025-01-01", "2025-12-31", false);

    assertEquals(1, result.size());
    assertEquals("P1", result.getFirst().code());
  }

  @Test
  void filterThProductByWorkerCodeAndDateBetween_withReverseDates() {
    var worker = new Worker("W1", "name", "email", "fn", "addr", "city", "nif", "stat");
    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M1", "M1", "Desc", 10, product);
    mission.add(
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 15), 1.0, "c1", Instant.now()));
    var service = createServiceWithProducts(List.of(product));

    var result =
        service.filterThProductByWorkerCodeAndDateBetween("W1", "2025-12-31", "2025-01-01", false);

    assertEquals(1, result.size());
  }

  @Test
  void filterThProductsByWorkerCode_withNullWorkerCode() {
    var worker = new Worker("W1", "name", "email", "fn", "addr", "city", "nif", "stat");
    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M1", "M1", "Desc", 10, product);
    mission.add(
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 15), 1.0, "c1", Instant.now()));
    var service = createServiceWithProducts(List.of(product));

    var result = service.filterThProductsByWorkerCode(null, false);

    assertFalse(result.isEmpty());
  }

  @Test
  void filterThProductsByWorkerCode_withNonNullWorkerCode() {
    var worker = new Worker("W1", "name", "email", "fn", "addr", "city", "nif", "stat");
    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M1", "M1", "Desc", 10, product);
    mission.add(
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 15), 1.0, "c1", Instant.now()));
    var service = createServiceWithProducts(List.of(product));

    var result = service.filterThProductsByWorkerCode("W1", false);

    assertEquals(1, result.size());
  }

  @Test
  void getAllThProducts_withoutUnpaidCareMissions() {
    var worker = new Worker("W1", "name", "email", "fn", "addr", "city", "nif", "stat");
    var careProduct = new Product("CARE", "Care", "Desc");
    var mission = new Mission("M1", "M1", "Desc", 10, careProduct);
    mission.add(
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 15), 1.0, "c1", Instant.now()));
    var service = createServiceWithProducts(List.of(careProduct));

    var result = service.getAllThProducts(true);

    assertEquals(1, result.size());
    assertTrue(result.getFirst().missions().isEmpty());
  }

  @Test
  void getAllThProducts_withUnpaidCareMissions() {
    var worker = new Worker("W1", "name", "email", "fn", "addr", "city", "nif", "stat");
    var careProduct = new Product("CARE", "Care", "Desc");
    var mission = new Mission("M1", "M1", "Desc", 10, careProduct);
    mission.add(
        new MissionExecution(mission, worker, LocalDate.of(2025, 6, 15), 1.0, "c1", Instant.now()));
    var service = createServiceWithProducts(List.of(careProduct));

    var result = service.getAllThProducts(false);

    assertEquals(1, result.size());
    assertFalse(result.getFirst().missions().isEmpty());
  }

  @Test
  void thProductsExecutedDaysSumByMonth() {
    var meJan =
        new ThMissionExecution("M1", "W1", LocalDate.of(2025, 1, 15), 0.5, "c1", false, false);
    var meFeb =
        new ThMissionExecution("M2", "W1", LocalDate.of(2025, 2, 10), 0.3, "c2", false, false);
    var mission = new ThMission("M1", "Title1", "Desc", List.of(meJan, meFeb), false, false);
    var product = new ThProduct("P1", "P1", "Desc", List.of(mission), false);

    var result =
        createServiceWithRealMappers().thProductsExecutedDaysSumByMonth(List.of(product), false);

    assertEquals(0.5, result.get("january"));
    assertEquals(0.3, result.get("february"));
  }
}
