package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogoutControllerTest {

  private final LogoutController controller = new LogoutController();

  @Test
  void casdoorLogoutPage_returns_casdoor_logout_view() {
    var result = controller.casdoorLogoutPage();
    assertEquals("casdoor-logout", result);
  }
}
