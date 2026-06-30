package school.hei.asa.mail;

import static jakarta.mail.Message.RecipientType.BCC;
import static jakarta.mail.Message.RecipientType.CC;
import static jakarta.mail.Message.RecipientType.TO;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.PojaGenerated;
import school.hei.asa.file.zip.FileTyper;

// imports supprimés (SES conservés en commentaire ci-dessous)
// import java.io.ByteArrayOutputStream;
// import java.nio.ByteBuffer;
// import software.amazon.awssdk.core.SdkBytes;
// import software.amazon.awssdk.services.ses.model.RawMessage;
// import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

@PojaGenerated
@Component
@AllArgsConstructor
public class Mailer implements Consumer<Email> {
  private final EmailConf emailConf;
  private final FileTyper fileTyper;

  @Override
  public void accept(Email email) {
    try {
      sendBySmtp(email);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private void sendBySmtp(Email email) throws MessagingException {
    var session = createSmtpSession();
    var mimeMessage = toMimeMessage(session, email);
    mimeMessage.setContent(toMimeMultipart(email));
    Transport.send(mimeMessage);
  }

  private Session createSmtpSession() {
    var props = new Properties();
    props.put("mail.smtp.host", emailConf.getSmtpHost());
    props.put("mail.smtp.port", emailConf.getSmtpPort());
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
    props.put("mail.smtp.ssl.trust", emailConf.getSmtpHost());
    props.put("mail.debug", "true");

    var session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(
            emailConf.getSmtpUsername(), emailConf.getSmtpPassword());
      }
    });
    session.setDebug(true);
    return session;
  }

  // Ancienne méthode d'envoi via AWS SES (conservée pour référence)
  // private void sendBySes(Email email) throws MessagingException, IOException {
  //   var session = Session.getDefaultInstance(new Properties());
  //   var mimeMessage = toMimeMessage(session, email);
  //   mimeMessage.setContent(toMimeMultipart(email));
  //
  //   var outputStream = new ByteArrayOutputStream();
  //   mimeMessage.writeTo(outputStream);
  //   ByteBuffer byteBuffer = ByteBuffer.wrap(outputStream.toByteArray());
  //   var bytes = new byte[byteBuffer.remaining()];
  //   byteBuffer.get(bytes);
  //
  //   var rawEmailRequest =
  //       SendRawEmailRequest.builder()
  //           .rawMessage(RawMessage.builder().data(SdkBytes.fromByteArray(bytes)).build())
  //           .build();
  //
  //   emailConf.getSesClient().sendRawEmail(rawEmailRequest);
  // }

  private MimeMessage toMimeMessage(Session session, Email email) throws MessagingException {
    var message = new MimeMessage(session);
    message.setFrom(new InternetAddress(emailConf.getSesSource()));
    message.setRecipients(TO, email.to().toString());
    message.setSubject(email.subject(), "UTF-8");
    message.setRecipients(CC, email.cc().toArray(InternetAddress[]::new));
    message.setRecipients(BCC, email.bcc().toArray(InternetAddress[]::new));
    return message;
  }

  private MimeMultipart toMimeMultipart(Email email) throws MessagingException {
    var htmlPart = new MimeBodyPart();
    htmlPart.setContent(
        email.htmlBody() == null ? "" : email.htmlBody(), "text/html; charset=UTF-8");
    List<MimeBodyPart> attachmentsAsMimeBodyParts =
        email.attachments().stream().map(this::toMimeBodyPart).toList();

    var mimeMultipart = new MimeMultipart("mixed");
    mimeMultipart.addBodyPart(htmlPart);
    attachmentsAsMimeBodyParts.forEach(mimeBodyPart -> addBodyPart(mimeMultipart, mimeBodyPart));
    return mimeMultipart;
  }

  private static void addBodyPart(MimeMultipart mimeMultipart, MimeBodyPart mimeBodyPart) {
    try {
      mimeMultipart.addBodyPart(mimeBodyPart);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private MimeBodyPart toMimeBodyPart(File attachment) {
    var mimeBodyPart = new MimeBodyPart();
    var fileMediaType = String.valueOf(fileTyper.apply(attachment));
    try {
      DataSource ds =
          new ByteArrayDataSource(Files.readAllBytes(attachment.toPath()), fileMediaType);
      mimeBodyPart.setDataHandler(new DataHandler(ds));
      mimeBodyPart.setFileName(attachment.getName());
      return mimeBodyPart;
    } catch (IOException | MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
