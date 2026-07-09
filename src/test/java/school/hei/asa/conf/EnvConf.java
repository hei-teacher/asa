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
    registry.add("ACCOUNTANTS", () -> "dummy,dummy2");
    registry.add("MAX_LATENESS_REPORT", () -> 4);
    registry.add("SENSITIVE_WORKERS_CODES", () -> "W-059,W-00");
    registry.add("asa.low.contract.days.threshold", () -> 10);
    registry.add("LOW_CONTRACT_DAYS_THRESOLD", () -> "10");
    registry.add("aws.region", () -> "eu-west-3");
    registry.add("aws.s3.bucket", () -> "dummy-bucket");
    registry.add("aws.eventBridge.bus", () -> "dummy-bus");
    registry.add("SMTP_HOST", () -> "dummy");
    registry.add("SMTP_PORT", () -> "587");
    registry.add("SMTP_USERNAME", () -> "dummy");
    registry.add("SMTP_PASSWORD", () -> "dummy");
    registry.add("SMTP_FROM", () -> "dummy@dummy.com");
  }
}
