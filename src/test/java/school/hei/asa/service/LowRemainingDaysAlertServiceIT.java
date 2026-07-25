package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.repository.jrepository.JContractRepository;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JContractLevel;
import school.hei.asa.repository.model.JWorker;

class LowRemainingDaysAlertServiceIT extends FacadeIT {

  @Autowired LowRemainingDaysAlertService lowRemainingDaysAlertService;
  @Autowired WorkerRepository workerRepository;
  @Autowired JContractRepository jContractRepository;

  @MockBean EventProducer eventProducer;

  Worker worker;

  @BeforeEach
  void setUp() {
    worker =
        new Worker(
            "alert-test-worker",
            "Test",
            "test@test.com",
            "Test Worker",
            "addr",
            "city",
            "nif",
            "stat");
    workerRepository.save(worker);
  }

  @Test
  void alert_triggered_when_remaining_days_below_threshold() {
    saveContract(5, "2026-07-01T00:00:00Z");

    var result = lowRemainingDaysAlertService.checkRemainingDaysAndBuildAlertMessage(worker);

    assertTrue(result.isPresent());
    assertTrue(result.get().contains("day(s) left"));
    verify(eventProducer).accept(any(List.class));
  }

  @Test
  void no_alert_when_remaining_days_above_threshold() {
    saveContract(15, "2026-08-01T00:00:00Z");

    var result = lowRemainingDaysAlertService.checkRemainingDaysAndBuildAlertMessage(worker);

    assertTrue(result.isEmpty());
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void no_alert_when_no_active_contract() {
    var result = lowRemainingDaysAlertService.checkRemainingDaysAndBuildAlertMessage(worker);

    assertTrue(result.isEmpty());
    verify(eventProducer, never()).accept(any());
  }

  private void saveContract(int durationInDays, String entranceInstantStr) {
    var jWorker = new JWorker();
    jWorker.setCode(worker.code());
    var level = new JContractLevel();
    level.setCode("L4P-2026");
    var jContract = new JContract();
    jContract.setId(UUID.randomUUID().toString());
    jContract.setWorker(jWorker);
    jContract.setLevel(level);
    jContract.setEntranceInstant(Instant.parse(entranceInstantStr));
    jContract.setEndInstant(null);
    jContract.setDurationInDays(durationInDays);
    jContract.setJobTitle("Test Job");
    jContract.setCompany("Test Company");
    jContract.setContractBucketKey("test-key");
    jContractRepository.save(jContract);
  }
}
