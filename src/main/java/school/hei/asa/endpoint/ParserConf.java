package school.hei.asa.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.hei.asa.number.NumberParser;

@Configuration
public class ParserConf {
  @Bean
  public NumberParser numberParser() {
    return new NumberParser();
  }
}
