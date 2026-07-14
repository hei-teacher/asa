package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.service.CalendarService;

class CalendarControllerTest {

  private final CalendarService calendarService = mock(CalendarService.class);
  private final WorkerFromAuthentication workerFromAuthentication =
      mock(WorkerFromAuthentication.class);
  private final WorkerToModelAdder workerToModelAdder = mock(WorkerToModelAdder.class);
  private final CalendarController controller =
      new CalendarController(calendarService, workerFromAuthentication, workerToModelAdder);

  @Test
  void getCalendar_with_null_year_uses_current_year() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var authentication = mock(Authentication.class);
    var model = new ConcurrentModel();
    int currentYear = LocalDate.now().getYear();

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), any(Model.class)))
        .thenReturn(worker);
    when(calendarService.missionExecutionPercentageSumByMissionType(worker, currentYear))
        .thenReturn(Map.of());
    when(calendarService.lateReportedDaysByMonth(worker, currentYear)).thenReturn(Map.of());
    when(calendarService.datesByDailyExecutionType(worker, currentYear))
        .thenReturn(
            Map.of(
                DailyExecution.Type.fullWork,
                List.of(),
                DailyExecution.Type.fullCare,
                List.of(),
                DailyExecution.Type.mixedWorkAndCare,
                List.of()));

    var result = controller.getCalendar(model, authentication, null, null);

    assertEquals("calendar", result);
    assertEquals(currentYear, model.getAttribute("year"));
    assertEquals("W-001", model.getAttribute("workerCode"));
    assertEquals(currentYear, model.getAttribute("currentYear"));
    assertNotNull(model.getAttribute("thYear"));
  }

  @Test
  void getCalendar_with_specific_year_uses_provided_year() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var authentication = mock(Authentication.class);
    var model = new ConcurrentModel();

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), any(Model.class)))
        .thenReturn(worker);
    when(calendarService.missionExecutionPercentageSumByMissionType(worker, 2024))
        .thenReturn(Map.of());
    when(calendarService.lateReportedDaysByMonth(worker, 2024)).thenReturn(Map.of());
    when(calendarService.datesByDailyExecutionType(worker, 2024))
        .thenReturn(
            Map.of(
                DailyExecution.Type.fullWork,
                List.of(),
                DailyExecution.Type.fullCare,
                List.of(),
                DailyExecution.Type.mixedWorkAndCare,
                List.of()));

    var result = controller.getCalendar(model, authentication, null, 2024);

    assertEquals("calendar", result);
    assertEquals(2024, model.getAttribute("year"));
  }
}
