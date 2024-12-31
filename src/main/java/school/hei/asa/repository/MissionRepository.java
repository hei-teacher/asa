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
    jMissionRepository.save(missionMapper.toEntity(mission));
  }

  @Transactional
  public void saveAll(List<Mission> missions) {
    missions.forEach(this::save);
  }

  @Transactional
  public Optional<Mission> findByCode(String code) {
    // TODO: distinguish MissionExecution from DailyExecution
    //   DailyExecution \in domain: it has the sum=1 validation rule
    //   MissionExecution \MIGHT_NOT \in domain: it CAN be persistence-specific
    //     In particular: JDailyExecution = { MissionExecution }
    return jMissionRepository.findByCode(code).map(missionMapper::toDomain);
  }

  @Transactional
  public List<Mission> findAll() {
    return jMissionRepository.findAll().stream().map(missionMapper::toDomain).toList();
  }
}
