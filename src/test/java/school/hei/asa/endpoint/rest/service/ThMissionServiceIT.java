package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;

class ThMissionServiceIT extends FacadeIT {
  @Autowired ThMissionService thMissionService;

  @Test
  void filter_ThMissions() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var thMissions = thMissionService.getUniqueMissionsByTitle(thProducts);

    assertEquals(4, thMissions.size());
  }

  @Test
  void filter_ThMissions_with_same_title() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var thMissions = thMissionService.getAllMissionsFromProducts(thProducts);

    assertEquals(2, thMissions.size());
  }
}
