package school.hei.asa.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

@Controller
@AllArgsConstructor
public class WorkerController {

  private final WorkerRepository workerRepository;

  @GetMapping("/workers")
  public List<Worker> getWorkers() {
    return workerRepository.findAll();
  }

  @GetMapping("/worker")
  public String getWorker() {
    return "worker";
  }
}
