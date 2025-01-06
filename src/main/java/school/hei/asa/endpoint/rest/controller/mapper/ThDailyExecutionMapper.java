package school.hei.asa.endpoint.rest.controller.mapper;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecution;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.MissionExecution;

@Component
public class ThDailyExecutionMapper {

  public ThDailyExecution toTh(LocalDate date, List<DailyExecution> deList) {
    return new ThDailyExecution(
        date, toTh(deList.stream().flatMap(de -> de.executions().stream()).toList()));
  }

  private List<ThMissionExecution> toTh(List<MissionExecution> meList) {
    List<ThMissionExecution> res = new ArrayList<>();
    var meByMission = meList.stream().collect(groupingBy(MissionExecution::mission));
    meByMission.forEach(
        (mission, missionExecutions) ->
            res.add(
                new ThMissionExecution(
                    mission,
                    missionExecutions.stream()
                        .sorted(comparing(me -> me.worker().name()))
                        .toList())));
    return res.stream().sorted(comparing(thMission -> thMission.mission().code())).toList();
  }
}
