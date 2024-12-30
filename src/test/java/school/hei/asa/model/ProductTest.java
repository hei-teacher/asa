package school.hei.asa.model;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  void productWithMissions() {
    var product = new Product("pcode", "pname", "pdescription");
    var mission1 = new Mission("mcode1", "mtitle1", "mdescription1", 10, product);
    var mission2 = new Mission("mcode2", "mtitle2", "mdescription2", 2, product);
    var student1 = new StudentContractor("scode", "sname");
    student1.execute(new DailyMissionExecution(student1, now(), Map.of(mission1, 1.)));

    assertEquals(12, product.maxDurationInDays());
    assertEquals(1, product.executedDays());
  }
}
