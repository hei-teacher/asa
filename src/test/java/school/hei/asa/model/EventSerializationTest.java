package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.hei.asa.endpoint.event.model.SendEmailRequested;

public class EventSerializationTest {
  ObjectMapper om = new ObjectMapper();

  @Test
  void refresh_org_billing_info_requested_serialization()
      throws JsonProcessingException, JsonProcessingException {
    var event = new SendEmailRequested();
    var serialized = om.writeValueAsString(event);
    var deserialized = om.readValue(serialized, SendEmailRequested.class);

    assertEquals(event, deserialized);
    assertNotNull(event.getCc());
    assertNotNull(event.getTo());
    assertNotNull(event.getBody());
    assertEquals(Duration.ofSeconds(45), event.maxConsumerDuration());
    assertEquals(Duration.ofSeconds(30), event.maxConsumerBackoffBetweenRetries());
  }
}
