package school.hei.asa.endpoint.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeaderController {
  @GetMapping("/headers")
  public String example(HttpServletRequest request) {
    String host = request.getHeader("X-Forwarded-Host");
    String proto = request.getHeader("X-Forwarded-Proto");
    return "Host: " + host + ", Proto: " + proto;
  }
}
