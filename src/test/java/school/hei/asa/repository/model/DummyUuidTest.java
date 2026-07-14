package school.hei.asa.repository.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DummyUuidTest {

  @Test
  void setId_and_getId_work() {
    var dummy = new DummyUuid();
    dummy.setId("test-uuid");
    assertEquals("test-uuid", dummy.getId());
  }
}
