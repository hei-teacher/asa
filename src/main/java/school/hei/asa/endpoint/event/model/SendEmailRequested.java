package school.hei.asa.endpoint.event.model;

import java.time.Duration;
import lombok.*;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.Worker;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class SendEmailRequested extends PojaEvent {
  private String to;
  private String cc;
  private String subject;
  private String body;

  private ThInvoiceForm invoiceForm;
  private Worker worker;
  private String month;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(45);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
