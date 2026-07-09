package school.hei.asa.endpoint.rest.controller.mapper;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThWorker;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;

@Slf4j
@AllArgsConstructor
@Component
public class ThWorkerMapper {

  public String toWorkerType(String contractType) {
    return switch (contractType) {
      case "partnerContractor" -> "Prestataire";
      case "fullTimeEmployee" -> "Salarié";
      case null -> "";
      default -> "Alternant";
    };
  }

  public ThWorker toThWorker(Worker worker, List<Contract> contracts) {
    var hasContract = !contracts.isEmpty();
    var entranceInstant = hasContract ? contracts.getLast().entranceInstant() : null;
    var level = hasContract ? contracts.getFirst().level().code() : null;
    var levelEntranceInstant = hasContract ? contracts.getFirst().entranceInstant() : null;
    var contractType = hasContract ? contracts.getFirst().level().type().name() : null;
    var workerType = toWorkerType(contractType);
    return new ThWorker(
        worker.code(),
        worker.name(),
        worker.email(),
        workerType,
        entranceInstant,
        level,
        levelEntranceInstant);
  }
}
