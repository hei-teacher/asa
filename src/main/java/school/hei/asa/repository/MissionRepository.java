package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Mission;
import school.hei.asa.repository.jrepository.JMissionRepository;
import school.hei.asa.repository.mapper.MissionMapper;

@AllArgsConstructor
@Repository
public class MissionRepository {
  private final JMissionRepository jMissionRepository;
  private final MissionMapper missionMapper;

  @Transactional
  public void save(Mission mission) {
    jMissionRepository.save(missionMapper.toEntity(mission, List.of()));
  }

  @Transactional
  public void saveAll(List<Mission> missions) {
    missions.forEach(this::save);
  }

  @Transactional
  public Optional<Mission> findByCode(String code) {
    return jMissionRepository.findByCode(code).map(missionMapper::toDomain);
  }
}
