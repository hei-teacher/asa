package school.hei.asa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.PaidCareMissionCodesSupplier;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MissionServiceIT extends FacadeIT {

  @Autowired MissionService missionService;

  @MockBean CareProductCodeSupplier careProductCodeSupplier;
  @MockBean PaidCareMissionCodesSupplier paidCareMissionCodesSupplier;

  @BeforeEach
  void setUp(){
    when(careProductCodeSupplier.get()).thenReturn("CA");
    when(paidCareMissionCodesSupplier.get()).thenReturn(List.of("CA-TEAMBUILDING"));
  }

  @Test
  void only_unpaidCare_are_unpaid(){
    setUp();
    var product = new Product("CA", "Care", "care product");
    var mission1 = new Mission("CA-TEAMBUILDING", "Team Building", "description1",100,product);
    var mission2 = new Mission("CA-DAYOFF", "Day Off", "description2",100,product);
    var worker = new Worker("workerCode", "name", "email", "fullname","address","city","nif","stat");
    var missionExecution1 = new MissionExecution(mission1,worker, LocalDate.now(),0.5d,"team building", Instant.now());
    var missionExecution2 = new MissionExecution(mission2,worker, LocalDate.now(),0.5d,"day off", Instant.now());

    assertFalse(missionService.isUnpaidCare(missionExecution1), "Team Building is paidCare");
    assertTrue(missionService.isUnpaidCare(missionExecution2), "Day off is unpaidCare");
  }
}
