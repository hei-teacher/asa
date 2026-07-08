package school.hei.asa.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.NearOverdueReportNotificationRequested;

class NearOverdueReportNotificationRequestedTest {

  @Test
  void check_default_values_and_durations() {
    var event = new NearOverdueReportNotificationRequested();
    assertNotNull(event.getVerificationDate());
    assertEquals(LocalDate.now(), event.getVerificationDate());
    assertEquals(Duration.ofSeconds(45), event.maxConsumerDuration());
    assertEquals(Duration.ofSeconds(30), event.maxConsumerBackoffBetweenRetries());
  }

  @Test
  void check_builder_and_to_builder() {
    LocalDate customDate = LocalDate.of(2026, 12, 25);
    var event =
        NearOverdueReportNotificationRequested.builder().verificationDate(customDate).build();

    assertEquals(customDate, event.getVerificationDate());
    var modifiedEvent = event.toBuilder().verificationDate(customDate.plusDays(1)).build();
    assertEquals(LocalDate.of(2026, 12, 26), modifiedEvent.getVerificationDate());
  }
}
