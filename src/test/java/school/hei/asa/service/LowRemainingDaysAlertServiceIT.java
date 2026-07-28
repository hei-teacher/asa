package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.model.Worker;

class LowRemainingDaysAlertServiceIT extends FacadeIT {

  @Autowired LowRemainingDaysAlertService lowRemainingDaysAlertService;

  @MockBean EventProducer eventProducer;

  @Test
  void alert_message_built_without_sending_email_when_remaining_days_below_threshold() {
    var result =
        lowRemainingDaysAlertService.verifyRemainingDaysAndBuildAlertMessage(
            workerBelowThreshold());

    assertTrue(result.isPresent());
    assertTrue(result.get().contains("day(s) left"));
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void send_alert_email_when_remaining_days_below_threshold() {
    lowRemainingDaysAlertService.sendAlertEmailIfLowRemainingDays(workerBelowThreshold());

    verify(eventProducer).accept(any(List.class));
  }

  @Test
  void no_alert_email_when_remaining_days_above_threshold() {
    lowRemainingDaysAlertService.sendAlertEmailIfLowRemainingDays(workerAboveThreshold());

    verify(eventProducer, never()).accept(any());
  }

  @Test
  void no_alert_when_remaining_days_above_threshold() {
    var result =
        lowRemainingDaysAlertService.verifyRemainingDaysAndBuildAlertMessage(
            workerAboveThreshold());

    assertTrue(result.isEmpty());
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void no_alert_when_no_active_contract() {
    var result =
        lowRemainingDaysAlertService.verifyRemainingDaysAndBuildAlertMessage(
            workerWithoutContract());

    assertTrue(result.isEmpty());
    verify(eventProducer, never()).accept(any());
  }

  private Worker workerBelowThreshold() {
    return new Worker("alert-worker-below", "Alert Worker Below", "", "", "", "", "", "");
  }

  private Worker workerAboveThreshold() {
    return new Worker("alert-worker-above", "Alert Worker Above", "", "", "", "", "", "");
  }

  private Worker workerWithoutContract() {
    return new Worker("alert-worker-none", "Alert Worker None", "", "", "", "", "", "");
  }
}
