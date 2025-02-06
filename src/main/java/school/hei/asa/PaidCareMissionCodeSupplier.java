package school.hei.asa;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Accessors(fluent = true)
@Configuration
public class PaidCareMissionCodeSupplier implements Supplier<List<String>> {
  private final List<String> paidCareMissionCodes;

  public PaidCareMissionCodeSupplier(
      @Value("${asa.paid.care.mission.code}") String paidCareMissionCode) {
    this.paidCareMissionCodes = Arrays.asList(paidCareMissionCode.split(","));
  }

  @Override
  public List<String> get() {
    return paidCareMissionCodes;
  }
}
