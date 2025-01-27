package school.hei.asa;

import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Accessors(fluent = true)
@Configuration
public class PaidCareProductCodeSupplier implements Supplier<String> {
  private final String paidCareProductCode;

  public PaidCareProductCodeSupplier(@Value("${asa.paid.care.mission.code}") String paidCareProductCode) {
    this.paidCareProductCode = paidCareProductCode;
  }

  @Override
  public String get() {
    return paidCareProductCode;
  }
}
