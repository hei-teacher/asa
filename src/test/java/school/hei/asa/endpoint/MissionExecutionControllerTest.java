package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.MissionExecutionController;
import school.hei.asa.endpoint.rest.model.th.ThDailyMissionExecutionForm;
import school.hei.asa.model.Mission;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.Product;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;

class MissionExecutionControllerTest extends FacadeIT {

  @Autowired MissionExecutionController missionExecutionController;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;

  @Test
  void save_then_read() {
    var worker = new PartnerContractor("worker-code", "name");
    workerRepository.save(worker);
    var product = new Product("pcode", "pname", "pdescription");
    productRepository.save(product);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));

    var dmeForm =
        new ThDailyMissionExecutionForm(
            "2024-12-01",
            "mission1-code",
            "0.2",
            "mission2-code",
            "0.8",
            null,
            null,
            null,
            null,
            null,
            null);
    missionExecutionController.createDailyMissionExecution(dmeForm, worker);

    var savedWorker = workerRepository.findByCode(worker.code());
    assertEquals(2, savedWorker.executionsByMission().keySet().size());
    var savedMission1 = missionRepository.findByCode("mission1-code");
    assertEquals(1, savedMission1.get().workers().size());
  }
}
