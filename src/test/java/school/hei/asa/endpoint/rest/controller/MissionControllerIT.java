package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Worker;

@Slf4j
public class MissionControllerIT extends FacadeIT {
  @Autowired MissionController missionController;
  @MockBean WorkerFromAuthentication workerFromAuthentication;
  Model model;
  Authentication authentication;
  Worker authenticatedWorker;

  @BeforeEach
  void setUp() {
    model = mock(Model.class);
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @AfterEach
  void resetRequestContext() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void can_get_all_missions() {
    authenticatedWorker =
        new Worker(
            "W-101",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");
    authentication = mock(Authentication.class);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    var viewName = missionController.getMissions(model, null, null, null, authentication);

    log.info("viewName = {}", viewName);

    verify(model).addAttribute(eq("workerCode"), eq(null));
    verify(model).addAttribute(eq("startDate"), eq(null));
    verify(model).addAttribute(eq("endDate"), eq(null));
    verify(model).addAttribute(eq("months"), any(Map.class));
    verify(model).addAttribute(eq("products"), any(List.class));
    verify(model).addAttribute(eq("total"), any(Map.class));
    verify(model).addAttribute(eq("worker"), eq(authenticatedWorker)); // actual worker
    verify(model).addAttribute(eq("workers"), any(List.class));
    verify(model).addAttribute(eq("workerName"), eq("John"));
    assertEquals("missions", viewName);
  }

  @Test
  void can_get_all_mission_executions_for_specific_yearMonth() {
    authenticatedWorker =
        new Worker(
            "W-101",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");
    authentication = mock(Authentication.class);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    var viewName = missionController.getMissionExecutions(model, null, "2024-07", authentication);

    verify(model).addAttribute(eq("dailyExecutions"), any(List.class));
    verify(model).addAttribute(eq("careProductCode"), any(String.class));
    verify(model).addAttribute(eq("yearMonth"), any(String.class));
    verify(model).addAttribute(eq("workerCode"), eq(null));

    assertEquals("mission-executions", viewName);
  }
}
