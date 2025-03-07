package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;

class MissionServiceIT extends FacadeIT {

  @Autowired MissionService missionService;

  @Test
  void thProductsExecutedDays_count_by_month() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false);
    var missionExecution3 =
        new ThMissionExecution("me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false);
    var mission1 =
        new ThMission("code1", "mission1", "description", List.of(missionExecution1), false);
    var mission2 =
        new ThMission("code2", "mission2", "description", List.of(missionExecution2), false);
    var mission3 =
        new ThMission("code3", "mission3", "description", List.of(missionExecution3), false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false));

    var thProducsByMonth = missionService.thProductsExecutedDaysSumByMonth(thProducts);

    var januaryExecutedDays = thProducsByMonth.get("january");
    var februaryExecutedDays = thProducsByMonth.get("february");
    assertEquals(0.5, januaryExecutedDays);
    assertEquals(1.5, februaryExecutedDays);
  }
}
