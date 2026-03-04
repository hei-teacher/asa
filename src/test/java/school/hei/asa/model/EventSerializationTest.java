package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.SendEmailRequested;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;

public class EventSerializationTest {
  ObjectMapper om = new ObjectMapper();

  @Test
  void refresh_org_billing_info_requested_serialization() throws JsonProcessingException {
    Worker worker =
        Worker.builder()
            .code("WRK-001")
            .name("Rakoto")
            .fullname("Jean Rakoto")
            .email("jean.rakoto@hei.school")
            .address("Lot IVG 156")
            .city("Antananarivo")
            .nif("1234567890")
            .stat("9876543210")
            .build();
    ThInvoiceForm invoice =
        new ThInvoiceForm(
            "inv_001",
            "2023-10",
            "REF-2023-001",
            "2023-10-25",
            "Frais de scolarité",
            "1",
            "100000",
            "100000",
            true,
            "Option Sport",
            "1",
            "20000",
            "20000",
            "120000",
            "Cent vingt mille",
            "RIB123456789");
    var event = new SendEmailRequested("", "", "", "", invoice, worker, "");

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
