package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.MissionController;

public class MissionControllerIT extends FacadeIT {
  @Autowired MissionController missionController;
  Model model;

  @BeforeEach
  void setUp() {
    model = mock(Model.class);
  }

  @Test
  void can_get_all_missions() {
    var viewName = missionController.getMissions(model, null, null, null);

    verify(model).addAttribute(eq("workerCode"), eq(null));
    verify(model).addAttribute(eq("startDate"), eq(null));
    verify(model).addAttribute(eq("endDate"), eq(null));
    verify(model).addAttribute(eq("months"), any(Map.class));
    verify(model).addAttribute(eq("products"), any(List.class));
    verify(model).addAttribute(eq("total"), any(Map.class));
    verify(model).addAttribute(eq("worker"), eq(null));
    verify(model).addAttribute(eq("workers"), any(Stream.class));
    verify(model).addAttribute(eq("workerName"), eq("All workers"));

    assertEquals("missions", viewName);
  }

  @Test
  void can_get_all_mission_executions_for_specific_yearMonth() {
    var viewName = missionController.getMissionExecutions(model, null, "2024-07");

    verify(model).addAttribute(eq("dailyExecutions"), any(List.class));
    verify(model).addAttribute(eq("careProductCode"), any(String.class));
    verify(model).addAttribute(eq("yearMonth"), any(String.class));
    verify(model).addAttribute(eq("workerCode"), eq(null));

    assertEquals("mission-executions", viewName);
  }
}
