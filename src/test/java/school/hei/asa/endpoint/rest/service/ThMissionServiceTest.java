package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionExecutionMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.MissionService;

class ThMissionServiceTest {

  private final ContractService contractService = mock(ContractService.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);
  private final ThMissionExecutionMapper missionExecutionMapper =
      new ThMissionExecutionMapper(contractService);

  @Test
  void getAllMissionsFromProducts_merges_missions_with_same_title() {
    var me1 =
        new ThMissionExecution("M1", "W1", LocalDate.of(2025, 1, 15), 0.5, "c1", false, false);
    var me2 =
        new ThMissionExecution("M1", "W1", LocalDate.of(2025, 2, 15), 0.3, "c2", false, false);
    var m1 = new ThMission("P01M1", "CommonTitle", "Desc1", List.of(me1), false, false);
    var m2 = new ThMission("P02M2", "CommonTitle", "Desc2", List.of(me2), false, false);
    var product1 = new ThProduct("P1", "P1", "Desc", List.of(m1), false);
    var product2 = new ThProduct("P2", "P2", "Desc", List.of(m2), false);

    var thMissionService = new ThMissionService(null, null);
    var result = thMissionService.getAllMissionsFromProducts(List.of(product1, product2));

    assertEquals(1, result.size());
    var merged = result.getFirst();
    assertEquals("M1", merged.getCode());
    assertEquals("CommonTitle", merged.getTitle());
    assertEquals(2, merged.getMissionExecutions().size());
    assertEquals(0.8, merged.executedDays(), 1e-9);
  }

  @Test
  void filterThMissionsByTitle_filters_by_given_title() {
    var me = new ThMissionExecution("M1", "W1", LocalDate.of(2025, 1, 15), 1.0, "c1", false, false);
    var m1 = new ThMission("M1", "Alpha", "Desc", List.of(me), false, false);
    var m2 = new ThMission("M2", "Beta", "Desc", List.of(me), false, false);
    var m3 = new ThMission("M3", "Alpha", "Desc", List.of(me), false, false);

    var thMissionService = new ThMissionService(null, null);
    var result = thMissionService.filterThMissionsByTitle(List.of(m1, m2, m3), "Alpha");

    assertEquals(2, result.size());
    assertEquals("M1", result.getFirst().getCode());
    assertEquals("M3", result.get(1).getCode());
  }

  @Test
  void filterThMissionsByDateBetween() {
    var me1 =
        new ThMissionExecution("M1", "W1", LocalDate.of(2025, 1, 15), 0.5, "c1", false, false);
    var me2 =
        new ThMissionExecution("M1", "W1", LocalDate.of(2025, 2, 15), 0.5, "c2", false, false);
    var mission = new ThMission("M1", "Mission1", "Desc", List.of(me1, me2), false, false);

    var thMissionService = new ThMissionService(null, null);
    var result =
        thMissionService.filterThMissionsByDateBetween(
            List.of(mission), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

    assertEquals(1, result.size());
    assertEquals(1, result.getFirst().getMissionExecutions().size());
  }

  @Test
  void filterThMissionExecutionsByDateBetween() {
    var me1 =
        new ThMissionExecution("M1", "W1", LocalDate.of(2025, 1, 15), 0.5, "c1", false, false);
    var me2 =
        new ThMissionExecution("M1", "W1", LocalDate.of(2025, 2, 15), 0.5, "c2", false, false);

    var thMissionService = new ThMissionService(null, null);
    var result =
        thMissionService.filterThMissionExecutionsByDateBetween(
            List.of(me1, me2), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));

    assertEquals(1, result.size());
  }

  @Test
  void getUniqueMissionsByTitle_returns_all_missions() {
    var me = new ThMissionExecution("M1", "W1", LocalDate.of(2025, 1, 15), 1.0, "c1", false, false);
    var m1 = new ThMission("M1", "Title1", "Desc", List.of(me), false, false);
    var m2 = new ThMission("M2", "Title2", "Desc", List.of(me), false, false);
    var p = new ThProduct("P1", "P1", "Desc", List.of(m1, m2), false);

    var thMissionService = new ThMissionService(null, null);
    var result = thMissionService.getUniqueMissionsByTitle(List.of(p));

    assertEquals(2, result.size());
  }

  @Test
  void toMissionChartData() {
    var me = new ThMissionExecution("M1", "W1", LocalDate.of(2025, 1, 15), 1.0, "c1", false, false);
    var mission = new ThMission("M1", "Title1", "Desc", List.of(me), false, false);

    var thMissionService = new ThMissionService(null, null);
    var result = thMissionService.toMissionChartData(List.of(mission));

    assertEquals(1, result.size());
    assertEquals("M1", result.getFirst().get("code"));
  }

  @Test
  void using_missionMapper_requires_deps() {
    when(contractService.getAllContractsByWorker(any())).thenReturn(List.of());
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());

    var missionMapper =
        new ThMissionMapper(
            missionExecutionMapper, careProductCodeSupplier, paidCareMissionCodesSupplier);
    var missionService = mock(MissionService.class);
    var thMissionService = new ThMissionService(missionService, missionMapper);

    var product = new school.hei.asa.model.Product("P1", "P1", "Desc");
    var mission = new school.hei.asa.model.Mission("M001", "Test", "Desc", 10, product);
    when(missionService.getAllMissions()).thenReturn(List.of(mission));

    var result = thMissionService.sortedMissionsWithoutMissionExecution();

    assertEquals(1, result.size());
    assertEquals("M001", result.getFirst().getCode());
  }
}
