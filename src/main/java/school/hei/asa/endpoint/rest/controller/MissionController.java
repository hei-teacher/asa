package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import lombok.AllArgsConstructor;
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

    var thProductsByWorkerCode =
        missionService.filterThProductByWorkerCodeAndDateBetween(workerCode, startDate, endDate);
    var thProductsByMonth = missionService.thProductsByMonth(thProductsByWorkerCode);
    var thProductsExecutedDaysSumByMonth =
        missionService.thProductsExecutedDaysSumByMonth(thProductsByWorkerCode);
    var thMissionsPerProductsByWorkerCode =
        missionService.filterThMissionsByWorkerCode(thProductsByWorkerCode);
    var thMissionsByWorkerCode = missionService.thMissionsByWorkerCode(thProductsByWorkerCode);

    List<Map<String, Object>> executedDaysByProduct = new ArrayList<>();
    for (var product : thProductsByWorkerCode) {
      Map<String, Object> dataPoint = new HashMap<>();
      dataPoint.put("code", product.code());
      dataPoint.put("name", product.name());
      dataPoint.put("executedDays", product.executedDays());
      dataPoint.put("studentExecutedDays", product.studentExecutedDays());
      executedDaysByProduct.add(dataPoint);
    }

    List<Map<String, Object>> executedDaysByProductMission = new ArrayList<>();
    for (var mission : thMissionsPerProductsByWorkerCode) {
      Map<String, Object> dataPoint = new HashMap<>();
      dataPoint.put("code", mission.getCode());
      dataPoint.put("name", mission.getTitle());
      dataPoint.put("executedDays", mission.executedDays());
      dataPoint.put("studentExecutedDays", mission.studentExecutedDays());
      executedDaysByProductMission.add(dataPoint);
    }

    List<Map<String, Object>> executedDaysByMission = new ArrayList<>();
    for (var mission : thMissionsByWorkerCode) {
      Map<String, Object> dataPoint = new HashMap<>();
      dataPoint.put("code", mission.getCode());
      dataPoint.put("name", mission.getTitle());
      dataPoint.put("executedDays", mission.executedDays());
      dataPoint.put("studentExecutedDays", mission.studentExecutedDays());
      executedDaysByMission.add(dataPoint);
    }

    model.addAttribute("workerCode", workerCode);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("months", thProductsByMonth);
    model.addAttribute("products", thProductsByWorkerCode);
    model.addAttribute("total", thProductsExecutedDaysSumByMonth);
    workerToModelAdder.apply(workerCode, model);
    model.addAttribute("executedDaysByProduct", executedDaysByProduct);
    model.addAttribute("executedDaysByProductMission", executedDaysByProductMission);
    model.addAttribute("executedDaysByMission", executedDaysByMission);

    return "missions";
  }

  @GetMapping("/missions/export-to-csv")
  public ResponseEntity<ByteArrayResource> exportToCSV(
      Model model, @RequestParam String workerCode) {

    var totalWorkDaysPerWorker = missionService.totalWorkDaysPerWorker(workerCode);
    String filePath = System.getProperty("java.io.tmpdir");
    String fileName =
        workerCode == null || workerCode.isBlank()
            ? "total_work_days-All.csv"
            : "total_work_days-"
                + totalWorkDaysPerWorker.keySet().stream().findFirst().get()
                + ".csv";
    File file = new File(filePath, fileName);
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write("worker,total_work_days,worker_level" + System.lineSeparator());
      fileWriter.flush();
      totalWorkDaysPerWorker.forEach(
          (workerName, totalWorkdays) -> {
            totalWorkdays.forEach(
                thWorkerLevelHistory -> {
                  try {
                    fileWriter.write(
                        workerName
                            + ","
                            + thWorkerLevelHistory.projectedDaysToWork()
                            + ","
                            + thWorkerLevelHistory.level()
                            + System.lineSeparator());
                    fileWriter.flush();
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                });
          });
      ByteArrayResource resource =
          new ByteArrayResource(Files.readAllBytes(Path.of(file.getAbsolutePath())));
      HttpHeaders header = new HttpHeaders();
      header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

      return ResponseEntity.ok()
          .headers(header)
          .contentLength(file.length())
          .contentType(MediaType.parseMediaType("application/octet-stream"))
          .body(resource);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
