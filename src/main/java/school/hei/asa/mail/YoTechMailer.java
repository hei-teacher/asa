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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.hei.asa.file.zip.FileTyper;

@Component
public class YoTechMailer implements Consumer<Email> {

  private final FileTyper fileTyper;

  @Value("${SMTP_HOST}")
  private String smtpHost;

  @Value("${SMTP_PORT}")
  private String smtpPort;

  @Value("${SMTP_USERNAME}")
  private String smtpUsername;

  @Value("${SMTP_PASSWORD}")
  private String smtpPassword;

  @Value("${SMTP_FROM}")
  private String smtpFrom;

  public YoTechMailer(FileTyper fileTyper) {
    this.fileTyper = fileTyper;
  }

  @Override
  public void accept(Email email) {
    try {
      send(email);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private void send(Email email) throws MessagingException {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);

    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(smtpUsername, smtpPassword);
      }
    });

    MimeMessage mimeMessage = toMimeMessage(session, email);
    mimeMessage.setContent(toMimeMultipart(email));

    Transport.send(mimeMessage);
  }

  private MimeMessage toMimeMessage(Session session, Email email) throws MessagingException {
    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(smtpFrom));
    message.setRecipients(TO, email.to().toString());
    message.setSubject(email.subject(), "UTF-8");
    if (email.cc() != null && !email.cc().isEmpty()) {
      message.setRecipients(CC, email.cc().toArray(InternetAddress[]::new));
    }
    if (email.bcc() != null && !email.bcc().isEmpty()) {
      message.setRecipients(BCC, email.bcc().toArray(InternetAddress[]::new));
    }
    return message;
  }

  private MimeMultipart toMimeMultipart(Email email) throws MessagingException {
    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(
        email.htmlBody() == null ? "" : email.htmlBody(), "text/html; charset=UTF-8");

    MimeMultipart mimeMultipart = new MimeMultipart("mixed");
    addBodyPart(mimeMultipart, htmlPart);

    if (email.attachments() != null) {
      List<MimeBodyPart> attachmentsAsMimeBodyParts =
          email.attachments().stream().map(this::toMimeBodyPart).toList();
      attachmentsAsMimeBodyParts.forEach(mimeBodyPart -> addBodyPart(mimeMultipart, mimeBodyPart));
    }

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
    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    String fileMediaType = String.valueOf(fileTyper.apply(attachment));
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
