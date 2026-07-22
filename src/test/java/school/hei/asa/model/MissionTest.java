package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.model.Mission.Type.paidCare;
import static school.hei.asa.model.Mission.Type.unpaidCare;
import static school.hei.asa.model.Mission.Type.work;

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
}
