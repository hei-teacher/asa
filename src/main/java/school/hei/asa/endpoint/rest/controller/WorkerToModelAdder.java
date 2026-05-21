package school.hei.asa.endpoint.rest.controller;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import school.hei.asa.model.Worker;
import school.hei.asa.service.SensitiveWorkerFilter;
import school.hei.asa.service.WorkerService;

@Slf4j
@Component
@AllArgsConstructor
public class WorkerToModelAdder implements BiFunction<String, Model, Worker> {
  private final WorkerService workerService;
  private final SensitiveWorkerFilter sensitiveWorkerFilter;

  @Override
  public Worker apply(String workerCode, Model model) {
    var worker =
        workerCode == null || workerCode.isBlank()
            ? null
            : workerService.findWorkerByCode(workerCode);
    var workers =
        sensitiveWorkerFilter.filterSensitiveWorkers(
            workerService.getWorkersFrom(model), workerCode);
    log.info(
        "there are {} workers = {}", workers.size(), workers.stream().map(Worker::name).toList());
    model.addAttribute("worker", worker);
    model.addAttribute("workerName", worker == null ? "All workers" : worker.name());
    model.addAttribute("workers", workers);
    return worker;
  }
}
