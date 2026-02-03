package school.hei.asa.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.repository.MissionRepository;

@Slf4j
@AllArgsConstructor
@Service
public class MissionService {
  private final MissionRepository missionRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final PaidCareMissionCodesSupplier paidCareMissionCodesSupplier;

  public List<Mission> getAllMissions() {
    return missionRepository.findAll();
  }

  public boolean isUnpaidCare(MissionExecution me){
    var mission = me.mission();
    log.info("here is the care product : {}", careProductCodeSupplier.get());
    log.info("here are the paid care mission : {}", paidCareMissionCodesSupplier.get());
    return mission.isCare(careProductCodeSupplier.get())
            && !mission.isPaidCare(paidCareMissionCodesSupplier.get());
  }
}
