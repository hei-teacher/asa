package school.hei.asa.endpoint.rest.controller.mapper;

import org.springframework.stereotype.Controller;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.StudentContractor;

@Controller
public class ThMissionExecutionMapper {

  public ThMissionExecution toTh(MissionExecution me, boolean isCare) {
    var worker = me.worker();
    return new ThMissionExecution(
        me.mission().code(),
        worker.code(),
        me.date(),
        me.dayPercentage(),
        me.comment(),
        isCare,
        worker instanceof StudentContractor);
  }
}
