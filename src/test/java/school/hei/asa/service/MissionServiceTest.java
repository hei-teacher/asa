package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;

class MissionServiceTest {

  private final MissionRepository missionRepository = mock(MissionRepository.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);
  private final MissionService missionService =
      new MissionService(missionRepository, careProductCodeSupplier, paidCareMissionCodesSupplier);

  @Test
  void getAllMissions() {
    var product = new Product("P1", "Product 1", "Desc");
    var missions = List.of(new Mission("M001", "Mission1", "Desc", 10, product));
    when(missionRepository.findAll()).thenReturn(missions);

    var result = missionService.getAllMissions();

    assertEquals(1, result.size());
  }

  @Test
  void isUnpaidCare_returns_true_for_care_mission_not_in_paid_list() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of());
    var careProduct = new Product("CARE", "Care Product", "Desc");
    var mission = new Mission("M001", "Care Mission", "Desc", 10, careProduct);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var me = new MissionExecution(mission, worker, LocalDate.now(), 0.5, "comment", Instant.now());

    assertTrue(missionService.isUnpaidCare(me));
  }

  @Test
  void isUnpaidCare_returns_false_for_non_care_mission() {
    when(careProductCodeSupplier.get()).thenReturn("CARE_CODE");
    var product = new Product("OTHER_PRODUCT", "Other Product", "Desc");
    var mission = new Mission("M001", "Other Mission", "Desc", 10, product);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var me = new MissionExecution(mission, worker, LocalDate.now(), 0.5, "comment", Instant.now());

    assertFalse(missionService.isUnpaidCare(me));
  }

  @Test
  void isUnpaidCare_returns_false_for_paid_care_mission() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of("PAID-001"));
    var careProduct = new Product("CARE", "Care Product", "Desc");
    var mission = new Mission("PAID-001", "Paid Care", "Desc", 10, careProduct);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var me = new MissionExecution(mission, worker, LocalDate.now(), 0.5, "comment", Instant.now());

    assertFalse(missionService.isUnpaidCare(me));
  }
}
