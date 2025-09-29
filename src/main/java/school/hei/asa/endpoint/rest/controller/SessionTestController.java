package school.hei.asa.endpoint.rest.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionTestController {

  @GetMapping("/test-session")
  public String testSession(HttpSession session) {
    if (session.isNew()) {
      session.setAttribute("testKey", "testValue");
      return "New session created: " + session.getId();
    }
    return "Existing session: " + session.getId() + ", testKey=" + session.getAttribute("testKey");
  }
}
