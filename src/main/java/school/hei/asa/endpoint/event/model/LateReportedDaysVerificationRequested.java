package school.hei.asa.endpoint.event.model;

import java.time.Duration;

public class LateReportedDaysVerificationRequested extends PojaEvent {
  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(45);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
