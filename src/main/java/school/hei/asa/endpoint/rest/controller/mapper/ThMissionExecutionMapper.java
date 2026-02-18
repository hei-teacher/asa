package school.hei.asa.endpoint.rest.controller.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.service.ThContractService;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;

@Slf4j
@Controller
@AllArgsConstructor
public class ThMissionExecutionMapper {
  private final ThContractService thContractService;

  public ThMissionExecution toTh(MissionExecution me, boolean isCare) {
    var worker = me.worker();
    return new ThMissionExecution(
        me.mission().code(),
        worker.code(),
        me.date(),
        me.dayPercentage(),
        me.comment(),
        isCare,
        isExecutedByStudent(worker, me));
  }

  private boolean isExecutedByStudent(Worker worker, MissionExecution me) {
    var contracts = thContractService.getAllContractsForWorker(worker);
    var dateFormater = DateTimeFormatter.ofPattern("dd MMM yyyy");
    return contracts.stream()
        .filter(
            contract -> {
              var entranceDate = LocalDate.parse(contract.entranceInstant(), dateFormater);
              return entranceDate.isBefore(me.date());
            })
        .max(Comparator.comparing(ThContract::entranceInstant))
        .get()
        .contractType()
        .equals("Alternant");
  }
}
