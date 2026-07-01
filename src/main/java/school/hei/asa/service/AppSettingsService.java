package school.hei.asa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppSettingsService {

  private final int lowContractDaysThreshold;

  public AppSettingsService(
      @Value("${asa.low.contract.days.threshold:10}") int lowContractDaysThreshold) {
    this.lowContractDaysThreshold = lowContractDaysThreshold;
  }

  public int getLowContractDaysThreshold() {
    return lowContractDaysThreshold;
  }
}
