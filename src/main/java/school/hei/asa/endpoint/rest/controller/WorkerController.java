package school.hei.asa.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThWorkerMapper;
import school.hei.asa.endpoint.rest.model.th.ThWorker;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.*;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

@Controller
@AllArgsConstructor
public class WorkerController {

  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final WorkerFromAuthentication workerFromAuthentication;
  private final WorkerToModelAdder workerToModelAdder;
  private final ThWorkerMapper thWorkerMapper;
  private final ThContractMapper thContractMapper;

  @GetMapping("/workers")
  public List<Worker> getWorkers() {
    return workerRepository.findAll();
  }

  @GetMapping("/worker")
  public String getWorker(
      Model model,
      Authentication authentication,
      @RequestParam(required = false) String workerCode) {
    var authenticatedWorker = workerFromAuthentication.apply(authentication).get().code();
    var workerToModelAdderParam = new WorkerModelAdderParam(workerCode, authenticatedWorker);

    var worker = workerToModelAdder.apply(workerToModelAdderParam, model);
    var contracts = contractRepository.findAllByWorker(worker);

    var hasContract = !contracts.isEmpty();
    var entranceInstant = hasContract ? contracts.getLast().entranceInstant() : null;
    var level = hasContract ? contracts.getFirst().level().code() : null;
    var levelEntranceInstant = hasContract ? contracts.getFirst().entranceInstant() : null;
    var contractType = hasContract ? contracts.getFirst().level().type().name() : null;
    var workerType = thWorkerMapper.toWorkerType(contractType);

    model.addAttribute(
        "worker",
        new ThWorker(
            worker.code(),
            worker.name(),
            worker.email(),
            workerType,
            entranceInstant,
            level,
            levelEntranceInstant));
    return "worker";
  }

  @GetMapping("/contracts")
  public String getContracts(
      Model model,
      Authentication authentication,
      @RequestParam(required = false) String workerCode) {
    var workerCodeOrAuth =
        workerCode == null || workerCode.isBlank()
            ? workerFromAuthentication.apply(authentication).get().code()
            : workerCode;

    var worker =
        workerToModelAdder.apply(
            new WorkerModelAdderParam(
                workerCodeOrAuth, workerFromAuthentication.apply(authentication).get().code()),
            model);
    var contracts = thContractMapper.toTh(contractRepository.findAllByWorker(worker));
    var hasMultipleContracts = contracts.size() > 1;
    var firstContractEnded =
        hasMultipleContracts || !contracts.getFirst().entranceInstant().equals("-");

    model.addAttribute("worker", worker);
    model.addAttribute("workerCode", workerCodeOrAuth);
    model.addAttribute("contracts", contracts);
    model.addAttribute("firstContractEnded", firstContractEnded);
    return "contracts";
  }
}
