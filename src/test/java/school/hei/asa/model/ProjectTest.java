package school.hei.asa.model;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProjectTest {

  @Test
  void projectWithMissions() {
    var mission1 = new Mission("mcode1", "mtitle1", "mdescription1", 10);
    var mission2 = new Mission("mcode2", "mtitle2", "mdescription2", 2);
    var student1 = new StudentContractor("scode", "sname");
    student1.execute(new DayExecution(student1, now(), Map.of(mission1, 1.)));

    var project = new Project("code", "description", Set.of(mission1, mission2));

    assertEquals(12, project.maxDurationInDays());
    assertEquals(1, project.executedDays());
  }
}
