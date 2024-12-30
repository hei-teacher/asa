package school.hei.asa.endpoint.rest.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.asa.model.Mission;
import school.hei.asa.repository.MissionRepository;

@RestController
@AllArgsConstructor
public class MissionController {

  private final MissionRepository missionRepository;

  @GetMapping("/missions")
  public List<Mission> getMissions() {
    return missionRepository.findAll();
  }
}
