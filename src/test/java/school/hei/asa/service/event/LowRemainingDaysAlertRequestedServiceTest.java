package school.hei.asa.service.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.InternetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.LowRemainingDaysAlertRequested;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.mapper.InternetAddressMapper;

class LowRemainingDaysAlertRequestedServiceTest {

  private Mailer mailer;
  private InternetAddressMapper internetAddressMapper;
  private ContractService contractService;
  private WorkerRepository workerRepository;
  private LowRemainingDaysAlertRequestedService service;

  @BeforeEach
  void setUp() throws Exception {
    mailer = mock(Mailer.class);
    internetAddressMapper = mock(InternetAddressMapper.class);
    contractService = mock(ContractService.class);
    workerRepository = mock(WorkerRepository.class);
    when(internetAddressMapper.toInternetAddresses(any()))
        .thenReturn(List.of(new InternetAddress("acc@test.com")));

    service =
        new LowRemainingDaysAlertRequestedService(
            mailer, "acc@test.com", 10, internetAddressMapper, contractService, workerRepository);
  }

  @Test
  void accept_sends_mail_for_worker() {
    var worker =
        new Worker("W-1", "Name", "email@test.com", "Full Name", "Addr", "City", "NIF", "STAT");
    var level = mock(ContractLevel.class);
    var contract =
        new Contract(
            worker,
            "Job",
            level,
            Instant.parse("2024-01-01T00:00:00Z"),
            null,
            Duration.ofDays(30),
            "Company",
            "key");
    when(workerRepository.findByCode("W-1")).thenReturn(worker);
    when(contractService.getActiveContractOrThrow(worker)).thenReturn(contract);

    service.accept(new LowRemainingDaysAlertRequested("W-1", 5));

    verify(mailer).accept(any());
  }
}
