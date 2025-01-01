package school.hei.asa.service;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Accessors(fluent = true)
@Getter
@Configuration
public class ProductConf {
  private final String careProductCode;

  public ProductConf(@Value("${asa.care.product.code}") String careProductCode) {
    this.careProductCode = careProductCode;
  }
}
