package school.hei.asa.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {

  public static final String DUMMY_CARE_PRODUCT_CODE = "dummy-care-product-code";
  public static final String DUMMY_PAID_CARE_MISSION_CODES = "dummy-paid-care-mission-codes";

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("asa.care.product.code", () -> DUMMY_CARE_PRODUCT_CODE);
    registry.add("asa.paid.care.mission.codes", () -> DUMMY_PAID_CARE_MISSION_CODES);
    registry.add("spring.security.oauth2.client.provider.casdoor.authorization-uri", () -> "dummy");
    registry.add("spring.security.oauth2.client.provider.casdoor.token-uri", () -> "dummy");
    registry.add("spring.security.oauth2.client.registration.casdoor.provider", () -> "casdoor");
    registry.add("spring.security.oauth2.client.registration.casdoor.client-id", () -> "dummy");
    registry.add(
        "spring.security.oauth2.client.registration.casdoor.redirect-uri",
        () -> "{baseUrl}/login/oauth2/code/casdoor");
    registry.add(
        "spring.security.oauth2.client.registration.casdoor.authorization-grant-type",
        () -> "authorization_code");
    registry.add("casdoor.logout.url", () -> "dummy");
    registry.add("asa.logout.url", () -> "dummy");
  }
}
