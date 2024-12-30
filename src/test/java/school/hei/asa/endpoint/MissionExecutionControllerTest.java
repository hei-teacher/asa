package school.hei.asa.endpoint;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.MissionExecutionController;
import school.hei.asa.model.DailyMissionExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.WorkerRepository;

class MissionExecutionControllerTest extends FacadeIT {

  @Autowired MissionExecutionController missionExecutionController;
  @Autowired MissionRepository missionRepository;
  @Autowired WorkerRepository workerRepository;

  @Test
  void save_then_read() {
    var worker = new PartnerContractor("worker-code", "name");
    workerRepository.save(worker);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2);
    missionRepository.saveAll(List.of(mission1, mission2));

    var dme = new DailyMissionExecution(worker, now(), Map.of(mission1, 0.2, mission2, 0.8));
    missionExecutionController.createMissionExecution(dme);

    var savedWorker = workerRepository.findByCode(worker.code());
    assertEquals(2, savedWorker.executionsByMission().keySet().size());
  }
}
