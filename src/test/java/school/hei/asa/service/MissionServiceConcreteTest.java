package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

class MissionServiceConcreteTest {

  private final MissionRepository missionRepository = mock(MissionRepository.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier =
      mock(PaidCareMissionCodesSupplier.class);
  private final MissionService service =
      new MissionService(missionRepository, careProductCodeSupplier, paidCareMissionCodesSupplier);

  private final Worker worker = new Worker("W-001", "J", "j@t.com", "J", "A", "C", "N", "S");

  @Test
  void isUnpaidCare_care_mission_not_in_paid_list() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of("PC"));
    var product = new Product("CARE", "Care", "D");
    var mission = new Mission("M01", "M", "D", 10, product);
    var me = new MissionExecution(mission, worker, LocalDate.now(), 1.0, "c", Instant.now());

    assertTrue(service.isUnpaidCare(me));
  }

  @Test
  void isUnpaidCare_care_mission_in_paid_list() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of("PC"));
    var product = new Product("PC", "Care", "D");
    var mission = new Mission("PC", "PaidCare", "D", 10, product);
    var me = new MissionExecution(mission, worker, LocalDate.now(), 1.0, "c", Instant.now());

    assertFalse(service.isUnpaidCare(me));
  }

  @Test
  void isUnpaidCare_non_care_mission() {
    when(careProductCodeSupplier.get()).thenReturn("CARE");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of("PC"));
    var product = new Product("WORK", "Work", "D");
    var mission = new Mission("W01", "Work", "D", 10, product);
    var me = new MissionExecution(mission, worker, LocalDate.now(), 1.0, "c", Instant.now());

    assertFalse(service.isUnpaidCare(me));
  }

  @Test
  void getAllMissions_delegates() {
    when(missionRepository.findAll()).thenReturn(List.of());
    assertTrue(service.getAllMissions().isEmpty());
  }
}
