package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PaidLeaveTest {

  @Test
  void left_is_not_taken_the_last_month_plus_base_minus_taken() {
    var paidLeave = new PaidLeave(2.5, 1.0, 3.0, 2.5);

    assertEquals(4.5, paidLeave.getLeft());
  }
}
