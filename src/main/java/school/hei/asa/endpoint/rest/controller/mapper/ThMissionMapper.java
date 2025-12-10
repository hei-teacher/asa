package school.hei.asa.endpoint.rest.controller.mapper;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.model.Mission;

@AllArgsConstructor
@Component
public class ThMissionMapper {

  private final ThMissionExecutionMapper missionExecutionMapper;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier;

  public ThMission toTh(Mission mission) {
    var isCare = mission.isCare(careProductCodeSupplier.get());
    var isUnpaidCare = isCare && !mission.isPaidCare(paidCareMissionCodesSupplier.get());
    return new ThMission(
        mission.code(),
        mission.title(),
        mission.description(),
        mission.executions().stream()
            .map(me -> missionExecutionMapper.toTh(me, isCare))
            .sorted(comparing(ThMissionExecution::getComment, naturalOrder()))
            .toList(),
        isCare,
        isUnpaidCare);
  }
}
