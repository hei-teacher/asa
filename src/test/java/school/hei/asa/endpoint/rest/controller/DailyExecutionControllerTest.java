package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.mapper.InternetAddressMapper;

class DailyExecutionControllerTest {

  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper =
      mock(ThDailyExecutionFormMapper.class);
  private final DailyExecutionRepository dailyExecutionRepository =
      mock(DailyExecutionRepository.class);
  private final WorkerFromAuthentication workerFromAuthentication =
      mock(WorkerFromAuthentication.class);
  private final ThMissionService thMissionService = mock(ThMissionService.class);
  private final ContractService contractService = mock(ContractService.class);
  private final Mailer mailer = mock(Mailer.class);
  private final InternetAddressMapper internetAddressMapper = mock(InternetAddressMapper.class);
  private final DailyExecutionController controller =
      new DailyExecutionController(
          thDailyExecutionFormMapper,
          dailyExecutionRepository,
          workerFromAuthentication,
          thMissionService,
          contractService,
          mailer,
          internetAddressMapper,
          "acc1@test.com",
          10);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

  @Test
  void getDailyExecutionForm_returns_view_with_missions() {
    var missions = List.of(new ThMission("M1", "Mission1", "Desc1", List.of(), false, false));
    when(thMissionService.sortedMissionsWithoutMissionExecution()).thenReturn(missions);
    var model = new ConcurrentModel();

    var viewName = controller.getDailyExecutionForm(model);

    assertEquals("daily-execution", viewName);
    assertSame(missions, model.getAttribute("missions"));
  }

  @Test
  void createDailyExecution_with_sufficient_remaining_days_no_alert() {
    var authentication = mock(Authentication.class);
    var form =
        new ThDailyExecutionForm(
            "2026-01-15",
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
            null,
            null,
            null,
            null);
    var redirectAttributes = new RedirectAttributesModelMap();
    var dailyExecution = mock(DailyExecution.class);

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(contractService.hasRemainingDays(worker)).thenReturn(true);
    when(thDailyExecutionFormMapper.toDomain(form, worker)).thenReturn(dailyExecution);
    when(contractService.remainingDays(worker)).thenReturn(15.0);

    var viewName = controller.createDailyExecution(authentication, form, redirectAttributes);

    assertEquals("redirect:/work-and-care-calendar", viewName);
    verify(dailyExecutionRepository).save(dailyExecution);
    assertNull(redirectAttributes.getFlashAttributes().get("contractAlert"));
    verify(mailer, never()).accept(any());
  }

  @Test
  void createDailyExecution_with_few_remaining_days_sends_alert() {
    var authentication = mock(Authentication.class);
    var form =
        new ThDailyExecutionForm(
            "2026-01-15",
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
            null,
            null,
            null,
            null);
    var redirectAttributes = new RedirectAttributesModelMap();
    var dailyExecution = mock(DailyExecution.class);

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(contractService.hasRemainingDays(worker)).thenReturn(true);
    when(thDailyExecutionFormMapper.toDomain(form, worker)).thenReturn(dailyExecution);
    when(contractService.remainingDays(worker)).thenReturn(3.0);
    var internetAddress = mock(jakarta.mail.internet.InternetAddress.class);
    when(internetAddressMapper.toInternetAddresses(anyList())).thenReturn(List.of(internetAddress));

    var viewName = controller.createDailyExecution(authentication, form, redirectAttributes);

    assertEquals("redirect:/work-and-care-calendar", viewName);
    verify(dailyExecutionRepository).save(dailyExecution);
    assertEquals(
        "Warning: only 3 days left on your contract.",
        redirectAttributes.getFlashAttributes().get("contractAlert"));
    verify(mailer).accept(any(Email.class));
  }

  @Test
  void createDailyExecution_with_no_remaining_days_throws() {
    var authentication = mock(Authentication.class);
    var form =
        new ThDailyExecutionForm(
            "2026-01-15",
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
            null,
            null,
            null,
            null);
    var redirectAttributes = new RedirectAttributesModelMap();

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(contractService.hasRemainingDays(worker)).thenReturn(false);

    assertThrows(
        IllegalArgumentException.class,
        () -> controller.createDailyExecution(authentication, form, redirectAttributes));
    verify(dailyExecutionRepository, never()).save(any());
    verify(mailer, never()).accept(any());
  }
}
