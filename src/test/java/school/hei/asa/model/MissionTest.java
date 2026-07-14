package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.*;
import static school.hei.asa.model.Mission.Type.paidCare;
import static school.hei.asa.model.Mission.Type.unpaidCare;
import static school.hei.asa.model.Mission.Type.work;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class MissionTest {
  @Test
  void mission_has_0_executed_days_when_no_worker() {
    var product = new Product("pcode", "pname", "pdescription");
    var mission = new Mission("mission-code", "Titre", "Description", 10, product);
    assertEquals(0, mission.executedDays());
  }

  @Test
  void get_correct_mission_type() {
    var product1 = new Product("CA", "product1", "product1 description");
    var product2 = new Product("AC", "product2", "product2 description");

    var mission1 = new Mission("CA-ABNP", "title1", "description1", 10, product1);
    var mission2 = new Mission("Work", "title2", "description2", 10, product2);
    var mission3 = new Mission("CA-FO", "title2", "description2", 10, product1);

    assertEquals(unpaidCare, mission1.type("CA", List.of("CA-FO")));
    assertEquals(work, mission2.type("CA", List.of("CA-FO")));
    assertEquals(paidCare, mission3.type("CA", List.of("CA-FO")));
  }

  @Test
  void isPaidCare_returns_true_when_code_in_list() {
    var product = new Product("PCODE", "pname", "pdesc");
    var mission = new Mission("M001", "title", "desc", 10, product);
    assertTrue(mission.isPaidCare(List.of("M001", "M002")));
  }

  @Test
  void isPaidCare_returns_false_when_code_not_in_list() {
    var product = new Product("PCODE", "pname", "pdesc");
    var mission = new Mission("M001", "title", "desc", 10, product);
    assertFalse(mission.isPaidCare(List.of("M002", "M003")));
  }

  @Test
  void workers_returns_workers_from_executions() {
    var product = new Product("PCODE", "pname", "pdesc");
    var mission = new Mission("M001", "title", "desc", 10, product);
    var worker = new Worker("W001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var execution =
        new MissionExecution(mission, worker, LocalDate.now(), 1.0, "comment", Instant.now());
    mission.add(execution);
    var workers = mission.workers();
    assertEquals(1, workers.size());
    assertTrue(workers.contains(worker));
  }

  @Test
  void executedDays_with_workers() {
    var product = new Product("PCODE", "pname", "pdesc");
    var mission = new Mission("M001", "title", "desc", 10, product);
    var worker = new Worker("W001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var execution =
        new MissionExecution(mission, worker, LocalDate.now(), 1.0, "comment", Instant.now());
    mission.add(execution);
    assertEquals(1.0, mission.executedDays());
  }

  @Test
  void add_with_wrong_mission_throws() {
    var product = new Product("PCODE", "pname", "pdesc");
    var mission1 = new Mission("M001", "title1", "desc1", 10, product);
    var mission2 = new Mission("M002", "title2", "desc2", 10, product);
    var worker = new Worker("W001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var execution =
        new MissionExecution(mission2, worker, LocalDate.now(), 1.0, "comment", Instant.now());
    assertThrows(IllegalArgumentException.class, () -> mission1.add(execution));
  }
}
