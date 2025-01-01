package school.hei.asa.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {

  public static final String DUMMY_CARE_PRODUCT_CODE = "dummy-care-product-code";

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("asa.care.product.code", () -> DUMMY_CARE_PRODUCT_CODE);
  }
}
