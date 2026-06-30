package school.hei.asa.mail;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import school.hei.asa.PojaGenerated;

// import org.springframework.context.annotation.Bean;
// import software.amazon.awssdk.regions.Region;
// import software.amazon.awssdk.services.ses.SesClient;

@PojaGenerated
@Configuration
public class EmailConf {

  @Getter private final String sesSource;
  @Getter private final String smtpHost;
  @Getter private final int smtpPort;
  @Getter private final String smtpUsername;
  @Getter private final String smtpPassword;

  public EmailConf(
      @Value("${aws.ses.source:noreply@poja.io}") String sesSource,
      @Value("${smtp.host:smtp.gmail.com}") String smtpHost,
      @Value("${smtp.port:587}") int smtpPort,
      @Value("${smtp.username:}") String smtpUsername,
      @Value("${smtp.password:}") String smtpPassword) {
    this.sesSource = sesSource;
    this.smtpHost = smtpHost;
    this.smtpPort = smtpPort;
    this.smtpUsername = smtpUsername;
    this.smtpPassword = smtpPassword;
  }

  // Ancienne méthode SES (conservée pour référence)
  // @Bean
  // public SesClient getSesClient() {
  //   var region = Region.of("eu-west-3");
  //   return SesClient.builder().region(region).build();
  // }
}
