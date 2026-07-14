package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProductConfTest {

  @Test
  void constructor_and_accessors_work() {
    var codes = List.of("MISSION1", "MISSION2");
    var conf = new ProductConf("CARE", codes);

    assertEquals("CARE", conf.careProductCode());
    assertEquals(codes, conf.paidCareMissionCodes());
  }
}
