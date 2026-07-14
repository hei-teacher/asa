package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HomeControllerTest {

  private final HomeController controller = new HomeController();

  @Test
  void getHome_returns_home_view() {
    var result = controller.getHome();
    assertEquals("home", result);
  }
}
