package school.hei.asa.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionExecutionMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionMapper;
import school.hei.asa.model.*;
import school.hei.asa.service.ContractService;

class ThMissionMapperTest {

  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);
  private final ContractService contractService = mock(ContractService.class);

  @Test
  void toTh_with_executions() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var product = new Product("CARE", "Care Product", "Desc");
    var mission = new Mission("M001", "Mission1", "Desc", 10, product);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var me = new MissionExecution(mission, worker, LocalDate.now(), 0.5, "comment", Instant.now());
    mission.add(me);

    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var mapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);

    var result = mapper.toTh(mission);

    assertEquals("M001", result.getCode());
    assertEquals("Mission1", result.getTitle());
    assertTrue(result.isCare());
    assertTrue(result.isUnpaidCare());
    assertEquals(1, result.getMissionExecutions().size());
    assertEquals(0.5, result.executedDays());
  }

  @Test
  void toThWithoutMissionExecution() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var product = new Product("CARE", "Care Product", "Desc");
    var mission = new Mission("M001", "Mission1", "Desc", 10, product);

    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var mapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);

    var result = mapper.toThWithoutMissionExecution(mission);

    assertEquals("M001", result.getCode());
    assertTrue(result.isCare());
    assertTrue(result.getMissionExecutions().isEmpty());
  }

  @Test
  void toTh_paid_care_product() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of("PAID-001"));
    var product = new Product("CARE", "Care Product", "Desc");
    var mission = new Mission("PAID-001", "Paid Care", "Desc", 10, product);

    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    var missionExecutionMapper = new ThMissionExecutionMapper(contractService);
    var mapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);

    var result = mapper.toThWithoutMissionExecution(mission);

    assertTrue(result.isCare());
    assertFalse(result.isUnpaidCare());
  }
}
