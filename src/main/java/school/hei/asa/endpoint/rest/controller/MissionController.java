package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecution;
import school.hei.asa.model.DailyExecution;
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
    var dailyExecutionsByDate =
        dailyExecutionRepository.findAll().stream().collect(groupingBy(DailyExecution::date));
    var thDailyExecutions = new ArrayList<ThDailyExecution>();
    dailyExecutionsByDate.forEach(
        (date, deList) -> thDailyExecutions.add(thDailyExecutionMapper.toTh(date, deList)));
    model.addAttribute(
        "dailyExecutions",
        thDailyExecutions.stream().sorted(comparing(ThDailyExecution::date).reversed()).toList());
    model.addAttribute("careProductCode", productConf.careProductCode());
    return "mission-executions";
  }
}
