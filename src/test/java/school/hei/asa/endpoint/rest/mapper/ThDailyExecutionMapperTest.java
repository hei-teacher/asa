package school.hei.asa.endpoint.rest.mapper;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecution;
import school.hei.asa.endpoint.rest.model.th.ThExecutionsPerMission;
import school.hei.asa.model.*;

class ThDailyExecutionMapperTest {

  ThDailyExecutionMapper mapper = new ThDailyExecutionMapper();

  @Test
  void can_map_daily_executions_to_th() {
    var product = new Product("pcode", "pname", "pdescription");
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    var worker = new PartnerContractor("worker-code", "name", "email");
    var me1 =
        new MissionExecution(
            mission1, worker, LocalDate.of(2025, 1, 15), 0.4, "comment1", Instant.now());
    var me2 =
        new MissionExecution(
            mission2, worker, LocalDate.of(2025, 1, 15), 0.3, "comment2", Instant.now());
    var me3 =
        new MissionExecution(
            mission2, worker, LocalDate.of(2025, 1, 15), 0.3, "comment3", Instant.now());
    var dailyExecution = new DailyExecution(worker, now(), List.of(me1, me2, me3));
    LocalDate date = LocalDate.of(2025, 1, 15);

    ThDailyExecution result = mapper.toTh(date, List.of(dailyExecution));

    assertNotNull(result);
    assertEquals(date, result.date());
    assertEquals(2, result.thExecutionsPerMission().size());

    ThExecutionsPerMission thMission1 = result.thExecutionsPerMission().get(0);
    assertEquals("mission1-code", thMission1.mission().code());
    assertEquals(1, thMission1.executions().size());
    assertEquals("name", thMission1.executions().get(0).worker().name());

    ThExecutionsPerMission thMission2 = result.thExecutionsPerMission().get(1);
    assertEquals("mission2-code", thMission2.mission().code());
    assertEquals(2, thMission2.executions().size());
    assertEquals("name", thMission2.executions().get(1).worker().name());
  }
}
