package school.hei.asa.service.event;

import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.event.model.NearOverdueReportNotificationRequested;
import school.hei.asa.mail.Mailer;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.service.ContractService;
import school.hei.asa.service.mapper.InternetAddressMapper;

class NearOverdueReportNotificationRequestedServiceTest {

  @Test
  void accept_on_weekday_with_missing_workers_sends_emails() {
    var contractService = mock(ContractService.class);
    var missionExecutionRepository = mock(MissionExecutionRepository.class);
    var mailer = mock(Mailer.class);
    var internetAddressMapper = mock(InternetAddressMapper.class);
    var service =
        new NearOverdueReportNotificationRequestedService(
            contractService,
            missionExecutionRepository,
            mailer,
            "acc1@test.com",
            3,
            internetAddressMapper);

    var friday = LocalDate.of(2025, 7, 11);
    var event = new NearOverdueReportNotificationRequested(friday);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contractLevel = new ContractLevel("L1", ContractType.partnerContractor, null, 200.0);
    var contract =
        new Contract(
            worker,
            "Dev",
            contractLevel,
            Instant.now(),
            null,
            Duration.ofDays(365),
            "Company",
            "key");
    when(missionExecutionRepository.findWorkerCodesByDate(friday.minusDays(3)))
        .thenReturn(List.of());
    when(contractService.findActiveContracts()).thenReturn(List.of(contract));

    service.accept(event);

    verify(mailer).accept(any());
  }

  @Test
  void accept_on_weekend_does_nothing() {
    var contractService = mock(ContractService.class);
    var missionExecutionRepository = mock(MissionExecutionRepository.class);
    var mailer = mock(Mailer.class);
    var internetAddressMapper = mock(InternetAddressMapper.class);
    var service =
        new NearOverdueReportNotificationRequestedService(
            contractService,
            missionExecutionRepository,
            mailer,
            "acc1@test.com",
            3,
            internetAddressMapper);

    var saturday = LocalDate.of(2025, 7, 12);
    var event = new NearOverdueReportNotificationRequested(saturday);

    service.accept(event);

    verify(mailer, never()).accept(any());
  }

  @Test
  void accept_on_monday_does_nothing() {
    var contractService = mock(ContractService.class);
    var missionExecutionRepository = mock(MissionExecutionRepository.class);
    var mailer = mock(Mailer.class);
    var internetAddressMapper = mock(InternetAddressMapper.class);
    var service =
        new NearOverdueReportNotificationRequestedService(
            contractService,
            missionExecutionRepository,
            mailer,
            "acc1@test.com",
            3,
            internetAddressMapper);

    var monday = LocalDate.of(2025, 7, 14);
    var event = new NearOverdueReportNotificationRequested(monday);

    service.accept(event);

    verify(mailer, never()).accept(any());
  }
}
