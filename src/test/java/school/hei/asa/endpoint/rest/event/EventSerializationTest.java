package school.hei.asa.endpoint.rest.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;

public class EventSerializationTest {
  ObjectMapper om = new ObjectMapper();

  @Test
  void can_serialize_event() throws JsonProcessingException {
    var event = new NewInvoiceGenerated("invoiceId");
    var serialized = om.writeValueAsString(event);
    var deserialized = om.readValue(serialized, NewInvoiceGenerated.class);
    assertEquals(event, deserialized);
    assertNotNull(event.getInvoiceId());
    assertEquals(Duration.ofSeconds(45), event.maxConsumerDuration());
    assertEquals(Duration.ofSeconds(30), event.maxConsumerBackoffBetweenRetries());
  }
}
