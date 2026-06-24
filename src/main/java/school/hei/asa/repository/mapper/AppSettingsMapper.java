package school.hei.asa.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.asa.model.AppSettings;
import school.hei.asa.repository.model.JAppSettings;

@Component
public class AppSettingsMapper {

  public AppSettings toDomain(JAppSettings jAppSettings) {
    return new AppSettings(jAppSettings.getId(), jAppSettings.getLowContractDaysThreshold());
  }

  public JAppSettings toEntity(AppSettings appSettings) {
    var jAppSettings = new JAppSettings();
    jAppSettings.setId(appSettings.id());
    jAppSettings.setLowContractDaysThreshold(appSettings.lowContractDaysThreshold());
    return jAppSettings;
  }
}
