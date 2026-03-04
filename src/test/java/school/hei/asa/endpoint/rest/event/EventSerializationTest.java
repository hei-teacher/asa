package school.hei.asa.endpoint.rest.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.SendEmailRequested;

public class EventSerializationTest {
  ObjectMapper om = new ObjectMapper();

  @Test
  void refresh_org_billing_info_requested_serialization() throws JsonProcessingException {

    var event = new SendEmailRequested("to", "cc", "workerCode", "yearMonth", "fileName");

    var serialized = om.writeValueAsString(event);
    var deserialized = om.readValue(serialized, SendEmailRequested.class);

    assertEquals(event, deserialized);
    assertNotNull(event.getCc());
    assertNotNull(event.getTo());
    assertNotNull(event.getYearMonth());
    assertEquals(Duration.ofSeconds(45), event.maxConsumerDuration());
    assertEquals(Duration.ofSeconds(30), event.maxConsumerBackoffBetweenRetries());
  }
}
