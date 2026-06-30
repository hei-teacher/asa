package school.hei.asa.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppSettingsService {

  private static final String DEFAULT_ID = "DEFAULT";
  private static final int DEFAULT_LOW_CONTRACT_DAYS_THRESHOLD = 10;

  private final AppSettingsRepository appSettingsRepository;

  public AppSettings getSettings() {
    return appSettingsRepository
        .findById(DEFAULT_ID)
        .orElseGet(
            () ->
                appSettingsRepository.save(
                    new AppSettings(DEFAULT_ID, DEFAULT_LOW_CONTRACT_DAYS_THRESHOLD)));
  }

  public int getLowContractDaysThreshold() {
    return getSettings().lowContractDaysThreshold();
  }

  public AppSettings updateLowContractDaysThreshold(int lowContractDaysThreshold) {
    return appSettingsRepository.save(new AppSettings(DEFAULT_ID, lowContractDaysThreshold));
  }
}
