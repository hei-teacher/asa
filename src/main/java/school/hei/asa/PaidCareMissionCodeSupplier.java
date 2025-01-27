package school.hei.asa;

import java.util.function.Supplier;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Accessors(fluent = true)
@Configuration
public class PaidCareMissionCodeSupplier implements Supplier<String> {
  private final String paidCareMissionCode;

  public PaidCareMissionCodeSupplier(
      @Value("${asa.paid.care.mission.code}") String paidCareMissionCode) {
    this.paidCareMissionCode = paidCareMissionCode;
  }

  @Override
  public String get() {
    return paidCareMissionCode;
  }
}
