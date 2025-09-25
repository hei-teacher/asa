package school.hei.asa.endpoint.rest.security;

import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

  @Bean
  public CookieSameSiteSupplier applicationCookieSameSiteSupplier() {

    return CookieSameSiteSupplier.ofNone();
  }
}
