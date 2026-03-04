package school.hei.asa.endpoint.event.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Duration;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendEmailRequested extends PojaEvent {
  private String to;
  private String cc;
  private String workerCode;
  private String yearMonth;
  private String fileName;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(45);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
