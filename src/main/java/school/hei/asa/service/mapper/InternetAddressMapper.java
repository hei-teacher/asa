package school.hei.asa.service.mapper;

import jakarta.mail.internet.InternetAddress;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InternetAddressMapper {
  public List<InternetAddress> toInternetAddresses(List<String> emails) {
    return emails.stream()
        .map(
            mail -> {
              try {
                return new InternetAddress(mail);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            })
        .toList();
  }
}
