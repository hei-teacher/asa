package school.hei.asa.endpoint.rest.controller;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.model.Worker;
import school.hei.asa.service.SensitiveWorkerFilter;
import school.hei.asa.service.WorkerService;

@Slf4j
@Component
@AllArgsConstructor
public class WorkerToModelAdder implements BiFunction<WorkerModelAdderParam, Model, Worker> {
  private final WorkerService workerService;
  private final SensitiveWorkerFilter sensitiveWorkerFilter;

  @Override
  public Worker apply(WorkerModelAdderParam workerModelAdderParam, Model model) {
    var worker =
        workerModelAdderParam.workerCode() == null || workerModelAdderParam.workerCode().isBlank()
            ? workerService.findWorkerByCode(workerModelAdderParam.authenticatedWorkerCode())
            : workerService.findWorkerByCode(workerModelAdderParam.workerCode());
    var workersSensitiveWorkerFiltered =
        sensitiveWorkerFilter.filterSensitiveWorkers(
            workerService.getWorkersFrom(model), workerModelAdderParam.authenticatedWorkerCode());
    log.info(
        "there are {} workers = {}",
        workersSensitiveWorkerFiltered.size(),
        workersSensitiveWorkerFiltered.stream().map(Worker::name).toList());
    model.addAttribute("worker", worker);
    model.addAttribute("workerName", worker == null ? "All workers" : worker.name());
    model.addAttribute("workers", workersSensitiveWorkerFiltered);
    return worker;
  }
}
