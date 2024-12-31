package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.DailyExecutionController;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.endpoint.rest.security.SecurityConfig;
import school.hei.asa.model.Mission;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.Product;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;

class DailyExecutionControllerTest extends FacadeIT {

  @Autowired DailyExecutionController dailyExecutionController;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;

  @MockBean SecurityConfig securityConfig;

  @Test
  void save_then_read() {
    var worker = new PartnerContractor("worker-code", "name", "email");
    workerRepository.save(worker);
    var product = new Product("pcode", "pname", "pdescription");
    productRepository.save(product);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));

    var dmeForm =
        new ThDailyExecutionForm(
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
    dailyExecutionController.createDailyExecution(dmeForm, worker);

    var savedWorker = workerRepository.findByCode(worker.code());
    assertEquals(2, savedWorker.executionsByMission().keySet().size());
    var savedMission1 = missionRepository.findByCode("mission1-code");
    assertEquals(1, savedMission1.get().workers().size());
    var savedProduct = productRepository.findByCode("pcode");
    assertEquals(1, savedProduct.executedDays());
  }
}
