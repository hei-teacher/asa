package school.hei.asa.service.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class InternetAddressMapperTest {

  private final InternetAddressMapper mapper = new InternetAddressMapper();

  @Test
  void toInternetAddresses_converts_valid_emails() {
    var emails = List.of("test@example.com", "user@domain.com");
    var addresses = mapper.toInternetAddresses(emails);

    assertEquals(2, addresses.size());
    assertEquals("test@example.com", addresses.get(0).getAddress());
    assertEquals("user@domain.com", addresses.get(1).getAddress());
  }

  @Test
  void toInternetAddresses_returns_empty_for_empty_list() {
    var addresses = mapper.toInternetAddresses(List.of());
    assertTrue(addresses.isEmpty());
  }

  @Test
  void toInternetAddresses_preserves_order() {
    var emails = List.of("first@test.com", "second@test.com", "third@test.com");
    var addresses = mapper.toInternetAddresses(emails);

    assertEquals(3, addresses.size());
    assertEquals("first@test.com", addresses.get(0).getAddress());
    assertEquals("third@test.com", addresses.get(2).getAddress());
  }
}
