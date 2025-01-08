package school.hei.asa.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {

  public static final String DUMMY_CARE_PRODUCT_CODE = "dummy-care-product-code";

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("asa.care.product.code", () -> DUMMY_CARE_PRODUCT_CODE);
    registry.add("spring.security.oauth2.client.provider.cognito.authorization-uri", () -> "dummy");
    registry.add("spring.security.oauth2.client.provider.cognito.token-uri", () -> "dummy");
    registry.add("spring.security.oauth2.client.registration.cognito.provider", () -> "cognito");
    registry.add("spring.security.oauth2.client.registration.cognito.client-id", () -> "dummy");
    registry.add(
        "spring.security.oauth2.client.registration.cognito.redirect-uri",
        () -> "{baseUrl}/login/oauth2/code/cognito");
    registry.add(
        "spring.security.oauth2.client.registration.cognito.authorization-grant-type",
        () -> "authorization_code");
    registry.add("cognito.logout.url", () -> "dummy");
    registry.add("asa.logout.url", () -> "dummy");
  }
}
