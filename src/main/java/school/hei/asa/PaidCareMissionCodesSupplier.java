package school.hei.asa;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Accessors(fluent = true)
@Configuration
public class PaidCareMissionCodesSupplier implements Supplier<List<String>> {
  private final List<String> paidCareMissionCodes;

  /**
   * Constructs a supplier for paid care mission codes.
   *
   * @param paidCareMissionCodes A comma-separated string of paid care mission codes (e.g.,
   *     "PC,MC").
   */
  public PaidCareMissionCodesSupplier(
      @Value("${asa.paid.care.mission.codes}") String paidCareMissionCodes) {
    this.paidCareMissionCodes = Arrays.asList(paidCareMissionCodes.split(","));
  }

  @Override
  public List<String> get() {
    return paidCareMissionCodes;
  }
}
