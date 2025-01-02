package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecution;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.service.ProductConf;

@Controller
@AllArgsConstructor
public class MissionController {

  private final ProductRepository productRepository;
  private final DailyExecutionRepository dailyExecutionRepository;
  private final ProductConf productConf;
  private final ThDailyExecutionMapper thDailyExecutionMapper;

  @GetMapping("/missions")
  public String getMissions(Model model) {
    model.addAttribute("products", productRepository.findAll());
    return "missions";
  }

  @GetMapping("/mission-executions")
  public String getMissionExecutions(Model model) {
    var thDailyExecutions =
        dailyExecutionRepository.findAll().stream()
            .map(thDailyExecutionMapper::toTh)
            .sorted(comparing(ThDailyExecution::date))
            .toList();
    model.addAttribute("dailyExecutions", thDailyExecutions);
    model.addAttribute("careProductCode", productConf.careProductCode());
    return "mission-executions";
  }
}
