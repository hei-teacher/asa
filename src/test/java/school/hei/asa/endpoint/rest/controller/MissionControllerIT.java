package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;

@Slf4j
public class MissionControllerIT extends FacadeIT {
  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean WorkerToModelAdder workerToModelAdder;

  @Autowired MissionController missionController;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;
  Authentication authentication;
  Model model;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    var authenticatedWorker =
        new Worker(
            "W-038",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");
    var product = new Product("pcode", "pname", "pdescription");
    productRepository.save(product);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));
    model = mock(Model.class);
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));

    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    when(workerToModelAdder.apply(anyString(), any())).thenReturn(authenticatedWorker);
  }

  @AfterEach
  void resetRequestContext() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void can_get_all_missions() {
    var viewName = missionController.getMissions(model, null, null, null);

    verify(model).addAttribute(eq("workerCode"), eq(null));
    verify(model).addAttribute(eq("startDate"), eq(null));
    verify(model).addAttribute(eq("endDate"), eq(null));
    verify(model).addAttribute(eq("products"), any(List.class));
    verify(model).addAttribute(eq("executedDaysByProduct"), any(List.class));
    verify(model).addAttribute(eq("months"), any(Map.class));
    verify(model).addAttribute(eq("executedDaysByProductMission"), any(List.class));
    verify(model).addAttribute(eq("executedDaysByMission"), any(List.class));
    verify(model).addAttribute(eq("total"), any(Map.class));
    verify(workerToModelAdder).apply(eq(null), eq(model));

    assertEquals("missions", viewName);
  }

  @Test
  void can_get_all_mission_executions_for_specific_yearMonth() {
    var viewName =
        missionController.getMissionExecutions(authentication, model, "W-038", "2024-07");

    verify(model).addAttribute(eq("dailyExecutions"), any(List.class));
    verify(model).addAttribute(eq("careProductCode"), any(String.class));
    verify(model).addAttribute(eq("yearMonth"), any(String.class));
    verify(model).addAttribute(eq("workerCode"), eq("W-038"));

    assertEquals("mission-executions", viewName);
  }

  @Test
  void sensitive_worker_sees_own_executions_but_not_other_sensitive_workers() {
    var worker1 = new Worker("W-059", null, null, null, null, null, null, null);
    var worker2 = new Worker("W-038", null, null, null, null, null, null, null);
    var worker3 = new Worker("W-037", null, null, null, null, null, null, null);

    var de1 =
        new DailyExecution(
            worker1,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));
    var de2 =
        new DailyExecution(
            worker2,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));
    var de3 =
        new DailyExecution(
            worker3,
            LocalDate.now(),
            List.of(new MissionExecution(null, null, null, 1, null, null)));

    var sensitiveWorkers = List.of(worker2.code());
    var result =
        missionController.filterMissionExecutionsWithoutSensitiveWorkers(
            List.of(de1, de2, de3), sensitiveWorkers, worker1.code());

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(de -> de.worker().code().equals("W-059")));
    assertTrue(result.stream().anyMatch(de -> de.worker().code().equals("W-037")));
    assertFalse(result.stream().anyMatch(de -> de.worker().code().equals("W-038")));
  }
}
