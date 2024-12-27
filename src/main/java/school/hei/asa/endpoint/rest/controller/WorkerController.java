package school.hei.asa.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

@RestController
@AllArgsConstructor
public class WorkerController {

  private final WorkerRepository workerRepository;

  @GetMapping("/workers")
  public List<Worker> getWorkers() {
    return workerRepository.findAll();
  }
}
