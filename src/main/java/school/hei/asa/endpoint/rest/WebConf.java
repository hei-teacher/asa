package school.hei.asa.endpoint.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class WebConf {

  @Bean
  public ForwardedHeaderFilter forwardedHeaderFilter() {
    return new ForwardedHeaderFilter();
  }
}
