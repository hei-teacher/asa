package school.hei.asa.endpoint.rest.service;

import static java.time.Month.JUNE;
import static java.time.Month.NOVEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.DailyExecution.Type.fullCare;
import static school.hei.asa.model.DailyExecution.Type.fullWork;
import static school.hei.asa.model.DailyExecution.Type.mixedWorkAndCare;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.DailyExecutionController;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.SecurityConfig;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.service.CalendarService;

class CalendarServiceIT extends FacadeIT {
  @Autowired DailyExecutionController dailyExecutionController;
  @Autowired WorkerRepository workerRepository;

  @MockBean SecurityConfig securityConfig;
  @MockBean WorkerFromAuthentication workerFromAuthentication;

  Authentication authentication;
  String authenticatedWorkerCode = "worker-code";

  @Autowired CalendarService calendarService;

  @BeforeEach
  void setUp() {
    authentication = authentication();
  }

  @Test
  void datesByDailyExecution_by_fullWork() {
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
        new ThDailyExecutionForm(
            "2023-11-15",
            "mission1-code",
            "0.2",
            "missionComment1",
            "mission2-code",
            "0.8",
            "missionComment2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null),
        new RedirectAttributesModelMap());

    var worker = workerRepository.findByCode(authenticatedWorkerCode);
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, 2023);

    var fullWorkDates = datesByDailyExecutionType.get(fullWork);
    assertEquals(1, fullWorkDates.size());
    assertEquals(LocalDate.of(2023, NOVEMBER, 15), fullWorkDates.get(0));
    assertEquals(0, datesByDailyExecutionType.get(fullCare).size());
    assertEquals(0, datesByDailyExecutionType.get(mixedWorkAndCare).size());
  }

  @Test
  void datesByDailyExecution_by_fullCare() {
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
        new ThDailyExecutionForm(
            "2025-12-01",
            "careMission-code",
            "1",
            "missionComment1",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null),
        new RedirectAttributesModelMap());
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
        new ThDailyExecutionForm(
            "2025-10-01",
            "careMission-code",
            "1",
            "missionComment2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null),
        new RedirectAttributesModelMap());

    var worker = workerRepository.findByCode(authenticatedWorkerCode);
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, 2025);
    assertEquals(2, datesByDailyExecutionType.get(fullCare).size());
    assertEquals(0, datesByDailyExecutionType.get(fullWork).size());
    assertEquals(0, datesByDailyExecutionType.get(mixedWorkAndCare).size());
  }

  @Test
  void datesByDailyExecution_by_mixedWorkAndCare() {
    dailyExecutionController.createDailyExecutionWithRedirectAttributes(
        authentication,
        new ThDailyExecutionForm(
            "2026-06-01",
            "mission1-code",
            "0.2",
            "missionComment1",
            "careMission-code",
            "0.8",
            "missionComment2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null),
        new RedirectAttributesModelMap());

    var worker = workerRepository.findByCode(authenticatedWorkerCode);
    var datesByDailyExecutionType = calendarService.datesByDailyExecutionType(worker, 2026);

    assertEquals(0, datesByDailyExecutionType.get(fullCare).size());
    assertEquals(0, datesByDailyExecutionType.get(fullWork).size());
    var mixedDates = datesByDailyExecutionType.get(mixedWorkAndCare);
    assertEquals(1, mixedDates.size());
    assertEquals(LocalDate.of(2026, JUNE, 1), mixedDates.get(0));
  }

  private Authentication authentication() {
    var authentication = mock(Authentication.class);
    var authenticatedWorker = workerRepository.findByCode(authenticatedWorkerCode);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    return authentication;
  }
}
