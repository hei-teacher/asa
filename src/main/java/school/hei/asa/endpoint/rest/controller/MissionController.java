package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.service.ProductConf;

@Controller
@AllArgsConstructor
public class MissionController {

  private final ProductRepository productRepository;
  private final MissionExecutionRepository missionExecutionRepository;
  private final ProductConf productConf;

  @GetMapping("/missions")
  public String getMissions(Model model) {
    model.addAttribute("products", productRepository.findAll());
    return "missions";
  }

  @GetMapping("/mission-executions")
  public String getMissionExecutions(Model model) {
    var dailyExecutions =
        missionExecutionRepository.findAllDailyExecution().stream()
            .sorted(comparing(DailyExecution::date))
            .map(
                dailyExecution ->
                    new DailyExecution(
                        dailyExecution.worker(),
                        dailyExecution.date(),
                        dailyExecution.executions().stream()
                            .sorted(comparing(me -> me.mission().code()))
                            .toList()))
            .toList();
    model.addAttribute("dailyExecutions", dailyExecutions);
    model.addAttribute("careProductCode", productConf.careProductCode());
    return "mission-executions";
  }
}
