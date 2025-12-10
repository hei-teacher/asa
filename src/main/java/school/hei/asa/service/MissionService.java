package school.hei.asa.service;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThWorkerMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.endpoint.rest.model.th.ThWorkerLevelHistory;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerLevelHistoryRepository;
import school.hei.asa.repository.WorkerRepository;

@Slf4j
@AllArgsConstructor
@Service
public class MissionService {

  private final ProductRepository productRepository;
  private final ThProductMapper thProductMapper;
  private final WorkerRepository workerRepository;
  private final WorkerLevelHistoryRepository workerLevelHistoryRepository;
  private final ThWorkerMapper thWorkerMapper;

  private List<ThProduct> filterThProductsByWorkerCode(
      String workerCode, boolean noUnpaidCareMissions) {
    var thProducts =
        thProductMapper.toTh(productRepository.findAll()).stream()
            .map(
                p ->
                    new ThProduct(
                        p.code(),
                        p.name(),
                        p.description(),
                        p.missions().stream()
                            .filter(m -> !m.isUnpaidCare() || !noUnpaidCareMissions)
                            .toList(),
                        p.isCare()))
            .toList();
    return workerCode == null || workerCode.isBlank()
        ? thProducts.stream()
            .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
            .toList()
        : thProducts.stream()
            .map(p -> p.filterByWorkerCode(workerCode))
            .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
            .toList();
  }

  public List<ThProduct> filterThProductByWorkerCodeAndDateBetween(
      String workerCode, String startDate, String endDate, boolean noUnpaidCareMissions) {
    var thProducts = filterThProductsByWorkerCode(workerCode, noUnpaidCareMissions);
    if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
      return thProducts;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    var startLocalDate = LocalDate.parse(startDate, formatter);
    var endLocalDate = LocalDate.parse(endDate, formatter);
    if (endLocalDate.isBefore(startLocalDate)) {
      return thProducts;
    }
    log.info("filtering by date...");

    List<ThProduct> result = new ArrayList<>();
    thProducts.forEach(
        p -> {
          var missions = p.missions();
          List<ThMission> newMissions = new ArrayList<>();
          missions.forEach(
              m -> {
                List<ThMissionExecution> newMissionExecution =
                    m.getMissionExecutions().stream()
                        .filter(
                            me -> {
                              var isBetween =
                                  me.getDate().isAfter(startLocalDate)
                                      && me.getDate().isBefore(endLocalDate);
                              return isBetween
                                  || me.getDate().isEqual(startLocalDate)
                                  || me.getDate().isEqual(endLocalDate);
                            })
                        .toList();
                newMissions.add(
                    new ThMission(
                        m.getCode(),
                        m.getTitle(),
                        m.getDescription(),
                        newMissionExecution,
                        m.isCare(),
                        m.isUnpaidCare()));
              });
          result.add(new ThProduct(p.code(), p.name(), p.description(), newMissions, p.isCare()));
        });
    if (workerCode.isBlank() || workerCode == null) {
      return result.stream()
          .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
          .toList();
    }
    return result.stream()
        .map(p -> p.filterByWorkerCode(workerCode))
        .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
        .toList();
  }

  public Map<String, List<ThProduct>> thProductsByMonth(List<ThProduct> thProducts) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    Map<String, List<ThProduct>> res = new LinkedHashMap<>();
    months.forEach(
        (month) -> {
          List<ThProduct> monthProducts =
              thProducts.stream()
                  .map(p -> p.filterByMonth(month))
                  .filter(Objects::nonNull)
                  .toList();

          var hasExecutedDays =
              monthProducts.stream().mapToDouble(ThProduct::executedDays).sum() > 0;

          if (hasExecutedDays) {
            res.putIfAbsent(month.toString().toLowerCase(), monthProducts);
          }
        });
    return res;
  }

  public Map<String, Double> thProductsExecutedDaysSumByMonth(
      List<ThProduct> thProducts, boolean noUnpaidCareMissions) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    Map<String, Double> res = new LinkedHashMap<>();
    months.forEach(
        (month) -> {
          List<ThProduct> monthProducts =
              thProducts.stream()
                  .map(p -> p.filterByMonth(month))
                  .filter(Objects::nonNull)
                  .toList();

          var hasExecutedDays =
              monthProducts.stream().mapToDouble(ThProduct::executedDays).sum() > 0;

          if (hasExecutedDays) {
            res.putIfAbsent(
                month.toString().toLowerCase(),
                thProductsExecutedDaysSum(monthProducts, month, noUnpaidCareMissions));
          }
        });
    return res;
  }

  public Double thProductsExecutedDaysSum(
      List<ThProduct> thProducts, Month month, boolean noUnpaidCareMissions) {
    return thProducts.stream()
        .map(p -> p.filterByMonth(month))
        .flatMap(p -> p.missions().stream())
        .filter(m -> !m.isUnpaidCare() || !noUnpaidCareMissions)
        .mapToDouble(ThMission::executedDays)
        .sum();
  }

  public List<ThMission> getUniqueMissionsByTitle(List<ThProduct> thProducts) {
    List<ThMission> missions = new ArrayList<>();
    thProducts.forEach(p -> missions.addAll(p.missions()));
    return missions.stream()
        .sorted(comparing(ThMission::executedDays, naturalOrder()).reversed())
        .toList();
  }

  public List<ThMission> getAllMissionsFromProducts(List<ThProduct> thProducts) {
    List<ThMission> missions = new ArrayList<>();

    thProducts.forEach(
        p -> {
          p.missions()
              .forEach(
                  m -> {
                    var filteredMission =
                        missions.stream()
                            .filter(thMission -> thMission.getTitle().equals(m.getTitle()))
                            .toList();
                    if (filteredMission.isEmpty()) {
                      ThMission newMission =
                          new ThMission(
                              m.getCode().substring(3),
                              m.getTitle(),
                              m.getDescription(),
                              m.getMissionExecutions(),
                              m.isCare(),
                              m.isUnpaidCare());
                      missions.add(newMission);
                    } else {
                      try {
                        var mission = filteredMission.getFirst();
                        var index = missions.indexOf(mission);
                        var missionExecutions = new ArrayList<>(mission.getMissionExecutions());
                        missionExecutions.addAll(m.getMissionExecutions());
                        mission.setMissionExecutions(missionExecutions);
                        missions.set(index, mission);
                      } catch (Exception e) {
                        log.error("here is the error: {}", e.toString());
                      }
                    }
                  });
        });
    return missions.stream()
        .sorted(comparing(ThMission::executedDays, naturalOrder()).reversed())
        .toList();
  }

  public Map<Worker, List<ThWorkerLevelHistory>> totalWorkDaysForOneWorker(String workerCode) {
    Map<Worker, List<ThWorkerLevelHistory>> result = new HashMap<>();
    var worker = workerRepository.findByCode(workerCode);
    var workerLevelHistories =
        thWorkerMapper.toTh(workerLevelHistoryRepository.findAllByWorker(worker));
    result.put(worker, workerLevelHistories);
    log.info("result be like = {}", result);
    return result;
  }

  public Map<Worker, List<ThWorkerLevelHistory>> totalWorkDaysPerWorker() {
    Map<Worker, List<ThWorkerLevelHistory>> result = new HashMap<>();
    var workers = workerRepository.findAll().stream().sorted(comparing(Worker::name)).toList();
    workers.parallelStream()
        .forEach(
            worker -> {
              var workerLevelHistories =
                  thWorkerMapper.toTh(workerLevelHistoryRepository.findAllByWorker(worker));
              result.put(worker, workerLevelHistories);
            });
    return result;
  }

  public File generateCSV(String workerCode) {
    var totalWorkDaysPerWorker =
        workerCode == null || workerCode.isBlank()
            ? totalWorkDaysPerWorker()
            : totalWorkDaysForOneWorker(workerCode);
    String filePath = System.getProperty("java.io.tmpdir");
    String fileName =
        workerCode == null || workerCode.isBlank()
            ? "total_work_days-All.csv"
            : "total_work_days-"
                + totalWorkDaysPerWorker.keySet().stream().findFirst().get().name()
                + ".csv";
    File file = new File(filePath, fileName);
    writeToFile(file, totalWorkDaysPerWorker);
    return file;
  }

  @SneakyThrows
  private void writeToFile(
      File file, Map<Worker, List<ThWorkerLevelHistory>> totalWorkDaysPerWorker) {
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(
          "code,worker,worker level,start date,"
              + "contract duration (in days),"
              + "total days worked,remaining days"
              + System.lineSeparator());
      fileWriter.flush();
      totalWorkDaysPerWorker.forEach(
          (worker, thWorkerLevelHistories) -> {
            thWorkerLevelHistories.parallelStream()
                .forEach(
                    thWorkerLevelHistory -> {
                      try {
                        String remainingDays = remainingDaysToString(thWorkerLevelHistory);
                        String actualWorkedDays = actualWorkedDaysToString(thWorkerLevelHistory);
                        String startDate =
                            thWorkerLevelHistory
                                .entranceInstant()
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .toString();
                        String textToWrite =
                            String.format(
                                "%s,%s,%s,%s,%s,%s,%s",
                                worker.code(),
                                worker.name(),
                                thWorkerLevelHistory.level(),
                                startDate,
                                thWorkerLevelHistory.projectedDaysToWork(),
                                actualWorkedDays,
                                remainingDays);

                        fileWriter.write(textToWrite + System.lineSeparator());
                        fileWriter.flush();
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    });
          });

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String remainingDaysToString(ThWorkerLevelHistory thWorkerLevelHistory) {
    if (thWorkerLevelHistory.projectedDaysToWork().equals("-")) {
      return "-";
    } else {
      var number =
          (Double.parseDouble(thWorkerLevelHistory.projectedDaysToWork())
              - Double.parseDouble(thWorkerLevelHistory.actualWorkedDay()));
      var numberFormat = new DecimalFormat("#.0");
      return numberFormat.format(number);
    }
  }

  private String actualWorkedDaysToString(ThWorkerLevelHistory thWorkerLevelHistory) {
    return Double.parseDouble(thWorkerLevelHistory.actualWorkedDay()) == 0.0d
        ? "-"
        : thWorkerLevelHistory.actualWorkedDay();
  }
}
