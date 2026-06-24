package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.AppSettings;
import school.hei.asa.repository.jrepository.JAppSettingsRepository;
import school.hei.asa.repository.mapper.AppSettingsMapper;

@AllArgsConstructor
@Repository
public class AppSettingsRepository {

  private final JAppSettingsRepository jAppSettingsRepository;
  private final AppSettingsMapper appSettingsMapper;

  @Transactional
  public Optional<AppSettings> findById(String id) {
    return jAppSettingsRepository.findById(id).map(appSettingsMapper::toDomain);
  }

  @Transactional
  public AppSettings save(AppSettings appSettings) {
    var saved = jAppSettingsRepository.save(appSettingsMapper.toEntity(appSettings));
    return appSettingsMapper.toDomain(saved);
  }
}
