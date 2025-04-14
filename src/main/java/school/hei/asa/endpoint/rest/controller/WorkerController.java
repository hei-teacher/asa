package school.hei.asa.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;

@Controller
@AllArgsConstructor
public class WorkerController {

  private final WorkerRepository workerRepository;
  private final WorkerFromAuthentication workerFromAuthentication;

  @GetMapping("/workers")
  public List<Worker> getWorkers() {
    return workerRepository.findAll();
  }

  @GetMapping("/worker")
  public String getWorker(Model model,
                          Authentication authentication,
                          @RequestParam(required = false) String workerCode) {
    var workerCodeOrAuth =
            workerCode == null || workerCode.isBlank()
                    ? workerFromAuthentication.apply(authentication).get().code()
                    : workerCode;

    Worker worker = workerRepository.findByCode(workerCodeOrAuth);

    model.addAttribute("worker", worker);
    return "worker";
  }
}
