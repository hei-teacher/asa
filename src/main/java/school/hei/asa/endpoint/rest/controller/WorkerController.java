package school.hei.asa.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.endpoint.rest.model.th.ThWorker;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.WorkerLevelHistoryRepository;
import school.hei.asa.repository.WorkerRepository;

@Controller
@AllArgsConstructor
public class WorkerController {

  private final WorkerRepository workerRepository;
  private final WorkerLevelHistoryRepository workerLevelHistoryRepository;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;

  @GetMapping("/workers")
  public List<Worker> getWorkers() {
    return workerRepository.findAll();
  }

  @GetMapping("/worker")
  public String getWorker(
      Model model,
      Authentication authentication,
      @RequestParam(required = false) String workerCode) {
    var workerCodeOrAuth =
        workerCode == null || workerCode.isBlank()
            ? workerFromAuthentication.apply(authentication).get().code()
            : workerCode;

    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    List<WorkerLevelHistory> wlhList = workerLevelHistoryRepository.findAllByWorker(worker);

    var entranceInstant = wlhList.isEmpty() ? null : wlhList.getFirst().entranceInstant();
    var level = wlhList.isEmpty() ? null : wlhList.getLast().level();
    var levelEntranceInstant = wlhList.isEmpty() ? null : wlhList.getLast().entranceInstant();

    model.addAttribute(
        "worker",
        new ThWorker(
            worker.code(),
            worker.name(),
            worker.email(),
            entranceInstant,
            level,
            levelEntranceInstant));
    return "worker";
  }

  @GetMapping("/worker-level-history")
  public String getWorkersLevelHistory(
      Model model,
      Authentication authentication,
      @RequestParam(required = false) String workerCode) {
    var workerCodeOrAuth =
        workerCode == null || workerCode.isBlank()
            ? workerFromAuthentication.apply(authentication).get().code()
            : workerCode;

    var worker = workerToModelAdder.apply(workerCodeOrAuth, model);
    List<WorkerLevelHistory> wlhList = workerLevelHistoryRepository.findAllByWorker(worker);

    model.addAttribute("worker", worker);
    model.addAttribute("workerCode", workerCodeOrAuth);
    model.addAttribute("workerLevelHistory", wlhList);
    return "worker-level-history";
  }
}
