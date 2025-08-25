package school.hei.asa.endpoint.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {
  @GetMapping("/casdoor-logout")
  public String casdoorLogoutPage() {
    return "casdoor-logout";
  }
}
