package school.hei.asa.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.hei.asa.service.utils.NumberConverter;

@Configuration
public class ConverterConf {
  @Bean
  public NumberConverter numberConverter() {
    return new NumberConverter();
  }
}
