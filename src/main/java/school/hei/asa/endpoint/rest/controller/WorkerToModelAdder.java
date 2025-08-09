package school.hei.asa.endpoint.rest.controller;

import static java.util.Comparator.comparing;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

@Component
@AllArgsConstructor
public class WorkerToModelAdder implements BiFunction<String, Model, Worker> {

  private final WorkerRepository workerRepository;

  @Override
  public Worker apply(String workerCode, Model model) {
    var worker =
        workerCode == null || workerCode.isBlank() ? null : workerRepository.findByCode(workerCode);
    model.addAttribute("worker", worker);
    model.addAttribute("workerName", worker == null ? "All workers" : worker.name());
    model.addAttribute(
        "workers", workerRepository.findAll().stream().sorted(comparing(Worker::name)));
    return worker;
  }
}
