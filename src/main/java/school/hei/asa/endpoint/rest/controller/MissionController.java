package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecution;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThContractService;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.endpoint.rest.service.ThProductService;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.SensitiveWorkerFilter;

@Slf4j
@Controller
@AllArgsConstructor
public class MissionController {

  private final DailyExecutionRepository dailyExecutionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final ThDailyExecutionMapper thDailyExecutionMapper;
  private final WorkerToModelAdder workerToModelAdder;
  private final ThMissionService thMissionService;
  private final ThProductService thProductService;
  private final ThContractService thContractService;
  private final SensitiveWorkerFilter sensitiveWorkerFilter;
  private final WorkerFromAuthentication workerFromAuthentication;

  @GetMapping("/missions")
  public String getMissions(
      Model model,
      @RequestParam(required = false) String workerCode,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      Authentication authentication) {
    model.addAttribute("workerCode", workerCode);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    var authenticateWorkerCode = workerFromAuthentication.apply(authentication).get().code();
    var noUnpaidCareMissions = true;
    var thProductsByWorkerCode =
        thProductService.filterThProductByWorkerCodeAndDateBetween(
            workerCode, startDate, endDate, true);
    log.info("thProductsByWorkerCode = {}", thProductsByWorkerCode);
    model.addAttribute("products", thProductsByWorkerCode);
    model.addAttribute(
        "executedDaysByProduct", thProductService.toProductChartData(thProductsByWorkerCode));

    var thProductsByMonth = thProductService.thProductsByMonth(thProductsByWorkerCode);
    model.addAttribute("months", thProductsByMonth);

    var thMissionsPerProductsByWorkerCode =
        thMissionService.getUniqueMissionsByTitle(thProductsByWorkerCode);
    model.addAttribute(
        "executedDaysByProductMission",
        thMissionService.toMissionChartData(thMissionsPerProductsByWorkerCode));

    var thMissionsByWorkerCode =
        thMissionService.getAllMissionsFromProducts(thProductsByWorkerCode);
    model.addAttribute(
        "executedDaysByMission", thMissionService.toMissionChartData(thMissionsByWorkerCode));

    var thProductsExecutedDaysSumByMonth =
        thProductService.thProductsExecutedDaysSumByMonth(
            thProductsByWorkerCode, noUnpaidCareMissions);
    model.addAttribute("total", thProductsExecutedDaysSumByMonth);

    workerToModelAdder.apply(new WorkerModelAdderParam(workerCode, authenticateWorkerCode), model);
    return "missions";
  }

  @SneakyThrows
  @GetMapping("/missions/export-to-csv")
  public ResponseEntity<ByteArrayResource> exportToCSV(@RequestParam String workerCode) {
    var file = thContractService.generateCSV(workerCode);
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
      @RequestParam(required = false) String yearMonth,
      Authentication authentication) {
    var authenticatedWorker = workerFromAuthentication.apply(authentication).get().code();
    YearMonth month =
        (yearMonth == null || yearMonth.isBlank()) ? YearMonth.now() : YearMonth.parse(yearMonth);
    var dailyExecutionsByYearMonthSensitiveWorkerFiltered =
        dailyExecutionsByDate(workerCode, month).entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        sensitiveWorkerFilter.filterMissionExecutionsWithoutSensitiveWorkers(
                            entry.getValue(), authenticatedWorker)));
    var thDailyExecutions = new ArrayList<ThDailyExecution>();
    dailyExecutionsByYearMonthSensitiveWorkerFiltered.forEach(
        (date, deList) -> thDailyExecutions.add(thDailyExecutionMapper.toTh(date, deList)));

    model.addAttribute(
        "dailyExecutions",
        thDailyExecutions.stream().sorted(comparing(ThDailyExecution::date).reversed()).toList());
    model.addAttribute("careProductCode", careProductCodeSupplier.get());
    model.addAttribute("yearMonth", month.toString());
    model.addAttribute("year", month.getYear());
    model.addAttribute("workerCode", workerCode);
    workerToModelAdder.apply(new WorkerModelAdderParam(workerCode, authenticatedWorker), model);

    return "mission-executions";
  }

  private Map<LocalDate, List<DailyExecution>> dailyExecutionsByDate(
      String workerCode, YearMonth month) {
    LocalDate startDate = month.atDay(1);
    LocalDate endDate = month.atEndOfMonth();

    if (workerCode == null || workerCode.isBlank()) {
      return dailyExecutionRepository.findByDateBetween(startDate, endDate).stream()
          .collect(groupingBy(DailyExecution::date));
    }
    return dailyExecutionRepository
        .findByWorkerCodeAndDateBetween(workerCode, startDate, endDate)
        .stream()
        .collect(groupingBy(DailyExecution::date));
  }
}
