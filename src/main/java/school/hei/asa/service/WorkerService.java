package school.hei.asa.service;

import static java.util.Comparator.comparing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

@Slf4j
@Service
@AllArgsConstructor
public class WorkerService {
  private final WorkerRepository workerRepository;

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

  public List<Worker> getWorkersFrom(Model model) {
    Integer year = (Integer) model.getAttribute("year");
    var startYear = getYearFrom(model, "startDate");
    var endYear = getYearFrom(model, "endDate");

    if (year != null) {
      log.info("fetching workers from {}", year);
      return getAllWorkersFromYearBetween(year, year + 1);
    } else if (startYear != null && endYear != null && startYear < endYear) {
      log.info("fetching workers between {} and {}", startYear, endYear);
      return getAllWorkersFromYearBetween(startYear, endYear);
    }
    log.info("fetching all workers...");
    return getAllWorkers();
  }

  private Integer getYearFrom(Model model, String attributeName) {
    var date = model.getAttribute(attributeName);
    if (date == null) {
      return null;
    }
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(String.valueOf(date), formatter).getYear();
  }
}
