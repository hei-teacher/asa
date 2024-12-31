package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.repository.ProductRepository;

@Controller
@AllArgsConstructor
public class MissionController {

  private final ProductRepository productRepository;

  @GetMapping("/missions")
  public String getMissions(Model model) {
    model.addAttribute("products", productRepository.findAll());
    return "missions";
  }
}
