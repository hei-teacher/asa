package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.WorkerController;
import school.hei.asa.endpoint.rest.security.SecurityConfig;

class WorkerControllerTest extends FacadeIT {

  @Autowired WorkerController workerController;
  @MockBean SecurityConfig securityConfig;

  @Test
  void listWorkers() {
    assertTrue(workerController.getWorkers().toString().contains("Lita"));
  }
}
