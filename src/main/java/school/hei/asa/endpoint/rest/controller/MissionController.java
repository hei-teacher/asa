package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecution;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.MissionService;

@Slf4j
@Controller
@AllArgsConstructor
public class MissionController {

  private final DailyExecutionRepository dailyExecutionRepository;

  private final CareProductCodeSupplier careProductCodeSupplier;
  private final ThDailyExecutionMapper thDailyExecutionMapper;
  private final WorkerToModelAdder workerToModelAdder;
  private final MissionService missionService;

  @GetMapping("/missions")
  public String getMissions(
      Model model,
      @RequestParam(required = false) String workerCode,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate) {
    model.addAttribute("workerCode", workerCode);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);

    var noUnpaidCareMissions = true;
    var thProductsByWorkerCode =
        missionService.filterThProductByWorkerCodeAndDateBetween(
            workerCode, startDate, endDate, true);
    model.addAttribute("products", thProductsByWorkerCode);

    List<Map<String, Object>> executedDaysByProduct = new ArrayList<>();
    for (var product : thProductsByWorkerCode) {
      Map<String, Object> dataPoint = new HashMap<>();
      dataPoint.put("code", product.code());
      dataPoint.put("name", product.name());
      dataPoint.put("executedDays", product.executedDays());
      dataPoint.put("studentExecutedDays", product.studentExecutedDays());
      executedDaysByProduct.add(dataPoint);
    }
    model.addAttribute("executedDaysByProduct", executedDaysByProduct);

    var thProductsByMonth = missionService.thProductsByMonth(thProductsByWorkerCode);
    model.addAttribute("months", thProductsByMonth);

    var thMissionsPerProductsByWorkerCode =
        missionService.getUniqueMissionsByTitle(thProductsByWorkerCode);
    List<Map<String, Object>> executedDaysByProductMission =
        toListOfMap(thMissionsPerProductsByWorkerCode);
    model.addAttribute("executedDaysByProductMission", executedDaysByProductMission);

    var thMissionsByWorkerCode = missionService.getAllMissionsFromProducts(thProductsByWorkerCode);
    List<Map<String, Object>> executedDaysByMission = toListOfMap(thMissionsByWorkerCode);
    model.addAttribute("executedDaysByMission", executedDaysByMission);

    var thProductsExecutedDaysSumByMonth =
        missionService.thProductsExecutedDaysSumByMonth(
            thProductsByWorkerCode, noUnpaidCareMissions);
    model.addAttribute("total", thProductsExecutedDaysSumByMonth);

    workerToModelAdder.apply(workerCode, model);
    return "missions";
  }

  private List<Map<String, Object>> toListOfMap(List<ThMission> thMissions) {
    List<Map<String, Object>> res = new ArrayList<>();
    for (var mission : thMissions) {
      Map<String, Object> dataPoint = new HashMap<>();
      dataPoint.put("code", mission.getCode());
      dataPoint.put("name", mission.getTitle());
      dataPoint.put("executedDays", mission.executedDays());
      dataPoint.put("studentExecutedDays", mission.studentExecutedDays());
      res.add(dataPoint);
    }
    return res;
  }

  @SneakyThrows
  @GetMapping("/missions/export-to-csv")
  public ResponseEntity<ByteArrayResource> exportToCSV(@RequestParam String workerCode) {
    var file = missionService.generateCSV(workerCode);
    ByteArrayResource resource =
        new ByteArrayResource(Files.readAllBytes(Path.of(file.getAbsolutePath())));
    HttpHeaders header = new HttpHeaders();
    header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

    return ResponseEntity.ok()
        .headers(header)
        .contentLength(file.length())
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(resource);
  }

  @GetMapping("/mission-executions")
  public String getMissionExecutions(
      Model model,
      @RequestParam(required = false) String workerCode,
      @RequestParam(required = false) String yearMonth) {
    YearMonth month =
        (yearMonth == null || yearMonth.isBlank()) ? YearMonth.now() : YearMonth.parse(yearMonth);

    var dailyExecutionsByYearMonth = dailyExecutionsByDate(workerCode, month);
    var thDailyExecutions = new ArrayList<ThDailyExecution>();
    dailyExecutionsByYearMonth.forEach(
        (date, deList) -> thDailyExecutions.add(thDailyExecutionMapper.toTh(date, deList)));

    model.addAttribute(
        "dailyExecutions",
        thDailyExecutions.stream().sorted(comparing(ThDailyExecution::date).reversed()).toList());
    model.addAttribute("careProductCode", careProductCodeSupplier.get());
    model.addAttribute("month", month.toString());
    model.addAttribute("workerCode", workerCode);
    workerToModelAdder.apply(workerCode, model);

    return "mission-executions";
  }

  private Map<LocalDate, List<DailyExecution>> dailyExecutionsByDate(
      String workerCode, YearMonth month) {
    LocalDate startDate = month.atDay(1);
    LocalDate endDate = month.atEndOfMonth();

    if (workerCode == null || workerCode.isBlank()) {
      return dailyExecutionRepository.findByDateBetween(startDate, endDate).stream()
          .collect(groupingBy(DailyExecution::date));
    } else {
      return dailyExecutionRepository
          .findByWorkerCodeAndDateBetween(workerCode, startDate, endDate)
          .stream()
          .collect(groupingBy(DailyExecution::date));
    }
  }
}
