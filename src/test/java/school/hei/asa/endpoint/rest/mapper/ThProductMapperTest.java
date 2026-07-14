package school.hei.asa.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionExecutionMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.service.ContractService;

class ThProductMapperTest {

  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);
  private final ContractService contractService = mock(ContractService.class);

  @Test
  void toTh_single_product() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var missionMapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);
    var productMapper = new ThProductMapper(missionMapper, careProductCodeSupplier);

    var product = new Product("CARE", "Care Product", "Desc");
    var mission = new Mission("M001", "Mission1", "Desc", 10, product);

    var result = productMapper.toTh(product);

    assertEquals("CARE", result.code());
    assertEquals("Care Product", result.name());
    assertTrue(result.isCare());
    assertEquals(1, result.missions().size());
  }

  @Test
  void toTh_list_of_products() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var missionMapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);
    var productMapper = new ThProductMapper(missionMapper, careProductCodeSupplier);

    var p1 = new Product("P1", "Product 1", "Desc 1");
    var p2 = new Product("P2", "Product 2", "Desc 2");

    var result = productMapper.toTh(List.of(p1, p2));

    assertEquals(2, result.size());
  }

  @Test
  void toTh_non_care_product() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var missionMapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);
    var productMapper = new ThProductMapper(missionMapper, careProductCodeSupplier);

    var product = new Product("OTHER", "Other Product", "Desc");

    var result = productMapper.toTh(product);

    assertFalse(result.isCare());
  }
}
