package school.hei.asa.service.event;

import static org.mockito.Mockito.*;

import jakarta.mail.internet.InternetAddress;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.concurrency.Workers;
import school.hei.asa.endpoint.event.model.ContractAlertRequested;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.service.mapper.InternetAddressMapper;

class ContractAlertRequestedServiceTest {

  private final Workers workers = new Workers();

  @Test
  void accept_sends_alert_email_to_accountants() throws Exception {
    var mailer = mock(Mailer.class);
    var internetAddressMapper = mock(InternetAddressMapper.class);
    var service =
        new ContractAlertRequestedService(
            mailer, internetAddressMapper, workers, "acc1@test.com,acc2@test.com");

    when(internetAddressMapper.toInternetAddresses(anyList()))
        .thenReturn(
            List.of(new InternetAddress("acc1@test.com"), new InternetAddress("acc2@test.com")));

    var event =
        ContractAlertRequested.builder()
            .workerName("John")
            .workerEmail("john@test.com")
            .remainingDays(3.0)
            .build();

    service.accept(event);

    verify(mailer).accept(any(Email.class));
  }

  @Test
  void accept_singular_day() throws Exception {
    var mailer = mock(Mailer.class);
    var internetAddressMapper = mock(InternetAddressMapper.class);
    var service =
        new ContractAlertRequestedService(mailer, internetAddressMapper, workers, "acc@test.com");

    when(internetAddressMapper.toInternetAddresses(anyList()))
        .thenReturn(List.of(new InternetAddress("acc@test.com")));

    var event =
        ContractAlertRequested.builder()
            .workerName("John")
            .workerEmail("john@test.com")
            .remainingDays(1.0)
            .build();

    service.accept(event);

    verify(mailer).accept(any(Email.class));
  }

  @Test
  void accept_empty_accountants_does_not_send() {
    var mailer = mock(Mailer.class);
    var internetAddressMapper = mock(InternetAddressMapper.class);
    var service =
        new ContractAlertRequestedService(mailer, internetAddressMapper, workers, "acc@test.com");

    when(internetAddressMapper.toInternetAddresses(anyList())).thenReturn(List.of());

    var event =
        ContractAlertRequested.builder()
            .workerName("John")
            .workerEmail("john@test.com")
            .remainingDays(3.0)
            .build();

    service.accept(event);

    verify(mailer, never()).accept(any());
  }
}
