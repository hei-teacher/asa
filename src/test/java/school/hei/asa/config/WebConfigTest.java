package school.hei.asa.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.ForwardedHeaderFilter;

class WebConfigTest {

  @Test
  void forwardedHeaderFilter_should_not_be_null() {
    WebConfig subject = new WebConfig();
    ForwardedHeaderFilter filter = subject.forwardedHeaderFilter();

    assertNotNull(filter);
  }
}
