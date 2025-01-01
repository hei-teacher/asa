package school.hei.asa.endpoint.rest.controller.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.asa.PojaGenerated;

@PojaGenerated
@RestController
public class PingController {

  @GetMapping("/ping")
  public String ping() {
    return "pong";
  }
}
