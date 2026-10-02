package school.hei.asa.service.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeITMockedThirdParties;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.mail.Email;
import school.hei.asa.mail.Mailer;

class NewInvoiceGeneratedServiceIT extends FacadeITMockedThirdParties {
  private static final String BUCKET_KEY = "payslips/FDP_Numer_2026_W-MAIL-01_2026-04.pdf";

  @Autowired NewInvoiceGeneratedService subject;
  @MockBean Mailer mailerMock;

  private NewInvoiceGenerated event() {
    return NewInvoiceGenerated.builder()
        .invoiceId("whatever")
        .workerCode("W-MAIL-01")
        .bucketKey(BUCKET_KEY)
        .yearMonth("2026-04")
        .build();
  }

  @Test
  void downloads_the_bucket_key_carried_by_the_event() {
    var pdf = new File("pdf");
    when(bucketConfMock.download(BUCKET_KEY)).thenReturn(pdf);

    subject.accept(event());

    verify(bucketConfMock).download(eq(BUCKET_KEY));
  }

  @Test
  void mails_the_document_to_the_worker_and_the_accountants() {
    var pdf = new File("pdf");
    when(bucketConfMock.download(BUCKET_KEY)).thenReturn(pdf);
    var sent = ArgumentCaptor.forClass(Email.class);

    subject.accept(event());

    verify(mailerMock).accept(sent.capture());
    var email = sent.getValue();
    assertEquals(List.of(pdf), email.attachments());
    assertTrue(email.subject().contains("W-MAIL-01"));
    assertTrue(email.subject().contains("Mail Recipient"));
    assertTrue(email.subject().contains("2026-04"));
    assertEquals("dummy", email.to().toString());
    var cc = email.cc().stream().map(Object::toString).toList();
    assertTrue(cc.contains("mail.recipient@mail.hei.school"));
  }
}
