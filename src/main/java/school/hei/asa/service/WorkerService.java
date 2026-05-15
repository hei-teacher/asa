package school.hei.asa.service;

import static java.util.Comparator.comparing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

@Slf4j
@Service
public class WorkerService {
  private final WorkerRepository workerRepository;
  private final String sensitiveWokerCodes;

  public WorkerService(
      WorkerRepository workerRepository,
      @Value("${SENSITIVE_WORKERS_CODES}") String sensitiveWokerCodes) {
    this.workerRepository = workerRepository;
    this.sensitiveWokerCodes = sensitiveWokerCodes;
  }

  public Worker findWorkerByCode(String workerCode) {
    return workerRepository.findByCode(workerCode);
  }

  public List<Worker> getAllWorkers() {
    return workerRepository.findAll().stream().sorted(comparing(Worker::name)).toList();
  }

  public List<Worker> getAllWorkersFromYearBetween(int startYear, int endYear) {
    return workerRepository.findByYearBetween(startYear, endYear).stream()
        .sorted(comparing(Worker::name))
        .toList();
  }

  public List<Worker> getWorkersFrom(Model model, String authenticatedSensitiveWorkerCode) {
    var startYear = getYearFrom(model, "startDate");
    var endYear = getYearFrom(model, "endDate");
    var year = getYearFrom(startYear, endYear, model);

    if (year != null) {
      log.info("fetching workers from {}", year);
      return getAllWorkersFromYearBetween(year, year + 1);
    } else if (startYear != null && endYear != null && startYear < endYear) {
      log.info("fetching workers between {} and {}", startYear, endYear);
      return getAllWorkersFromYearBetween(startYear, endYear);
    }
    log.info("fetching all workers...");
    return getWorkersWithoutSensitiveWorkers(
        Arrays.stream(sensitiveWokerCodes.split(",")).toList(), authenticatedSensitiveWorkerCode);
  }

  private Integer getYearFrom(Model model, String attributeName) {
    var date = model.getAttribute(attributeName);
    if (date == null) {
      return null;
    }
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(String.valueOf(date), formatter).getYear();
  }

  private Integer getYearFrom(Integer startYear, Integer endYear, Model model) {
    if (startYear != null && startYear.equals(endYear)) {
      return startYear;
    }
    return (Integer) model.getAttribute("year");
  }

  public List<Worker> getWorkersWithoutSensitiveWorkers(
      List<String> sensitiveWorkerCodes, String authenticatedSensitiveWorkerCode) {
    var workers = getAllWorkers();

    log.info("sensitiveWorkerCodes: {}", sensitiveWorkerCodes);
    if (sensitiveWorkerCodes.contains(authenticatedSensitiveWorkerCode)) {
      var toExculde =
          sensitiveWorkerCodes.stream()
              .filter(workerCode -> !workerCode.equals(authenticatedSensitiveWorkerCode))
              .toList();
      return workers.stream().filter(worker -> !toExculde.contains(worker.code())).toList();
    }
    return workers.stream()
        .filter(worker -> !sensitiveWorkerCodes.contains(worker.code()))
        .toList();
  }
}
