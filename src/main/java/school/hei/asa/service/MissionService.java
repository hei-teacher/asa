package school.hei.asa.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.model.Mission;
import school.hei.asa.repository.MissionRepository;

@Slf4j
@AllArgsConstructor
@Service
public class MissionService {
  private final MissionRepository missionRepository;

  public List<Mission> getAllMissions() {
    return missionRepository.findAll();
  }
}
