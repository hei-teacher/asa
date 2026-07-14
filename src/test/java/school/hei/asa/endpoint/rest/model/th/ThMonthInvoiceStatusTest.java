package school.hei.asa.endpoint.rest.model.th;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class ThMonthInvoiceStatusTest {

  @Test
  void constructor_and_getters_work() {
    var yearMonth = YearMonth.of(2025, 6);
    var status = new ThMonthInvoiceStatus(yearMonth, true);

    assertEquals(yearMonth, status.yearMonth());
    assertTrue(status.hasInvoice());
  }

  @Test
  void hasInvoice_false() {
    var status = new ThMonthInvoiceStatus(YearMonth.of(2025, 6), false);
    assertFalse(status.hasInvoice());
  }
}
