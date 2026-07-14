package school.hei.asa.endpoint.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventStackTest {

  @Test
  void enum_values_are_present() {
    var values = EventStack.values();
    assertEquals(2, values.length);
    assertEquals(EventStack.EVENT_STACK_1, values[0]);
    assertEquals(EventStack.EVENT_STACK_2, values[1]);
  }

  @Test
  void sqsQueueUrl_is_null_when_env_not_set() {
    assertNull(EventStack.EVENT_STACK_1.getSqsQueueUrl());
    assertNull(EventStack.EVENT_STACK_2.getSqsQueueUrl());
  }
}
