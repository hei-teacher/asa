package school.hei.asa.endpoint.rest.controller.mapper;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.service.ContractService;

@Slf4j
@Controller
@AllArgsConstructor
public class ThMissionExecutionMapper {
  private final ContractService contractService;
  private final Map<String, List<Contract>> rawContractsCache = new ConcurrentHashMap<>();

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
    var contracts =
        rawContractsCache.computeIfAbsent(
            worker.code(),
            code -> contractService.getAllContractsForWorkerWithoutExecutions(worker));
    return contracts.stream()
        .filter(
            c ->
                c.entranceInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .isBefore(me.date()))
        .max(Comparator.comparing(Contract::entranceInstant))
        .map(c -> c.level().type().name().equals("studentContractor"))
        .orElse(false);
  }
}
