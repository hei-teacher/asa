package school.hei.asa.service.event;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.service.mapper.InternetAddressMapper;

@Service
public class NewInvoiceGeneratedService implements Consumer<NewInvoiceGenerated> {
  private final String accountants;
  private final Mailer mailer;
  private final BucketComponent bucketComponent;
  private final WorkerRepository workerRepository;
  private final InternetAddressMapper emailService;

  public NewInvoiceGeneratedService(
      @Value("${ACCOUNTANTS}") String accountants,
      Mailer mailer,
      BucketComponent bucketComponent,
      WorkerRepository workerRepository,
      InternetAddressMapper emailService) {
    this.accountants = accountants;
    this.mailer = mailer;
    this.bucketComponent = bucketComponent;
    this.workerRepository = workerRepository;
    this.emailService = emailService;
  }

  @Override
  public void accept(NewInvoiceGenerated event) {
    var worker = workerRepository.findByCode(event.getWorkerCode());

    var listEmailsWithWorkerEmail = String.format("%s,%s", accountants, worker.email());
    var emailList = Arrays.asList(listEmailsWithWorkerEmail.split(","));
    var internetAddresses = emailService.toInternetAddresses(emailList);

    File pdf = bucketComponent.download(event.getBucketKey());
    var email =
        new Email(
            internetAddresses.getFirst(),
            internetAddresses.stream().skip(1).toList(),
            List.of(),
            String.format("ASA PAYMENT DOCUMENT - %s - %s", worker.name(), event.getYearMonth()),
            String.format(
                "Hello,\n"
                    + " Please find attached your payment document for %s for the month of"
                    + " %s.Best regards,",
                worker.name(), event.getYearMonth()),
            List.of(pdf));

    mailer.accept(email);
  }
}
