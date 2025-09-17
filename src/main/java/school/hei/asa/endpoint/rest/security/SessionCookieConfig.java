package school.hei.asa.endpoint.rest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionCookieConfig {

  @Bean
  public DefaultCookieSerializer defaultCookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName("SESSION");
    serializer.setSameSite("None");
    serializer.setUseSecureCookie(true);
    return serializer;
  }
}
