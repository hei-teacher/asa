package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.DailyExecutionController;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;
import school.hei.asa.repository.ProductRepository;
import school.hei.asa.repository.WorkerRepository;

public class ProductServiceIT extends FacadeIT {
  @Autowired ProductService productService;
  @Autowired WorkerRepository workerRepository;
  @Autowired ProductRepository productRepository;
  @Autowired MissionRepository missionRepository;
  @Autowired DailyExecutionController dailyExecutionController;

  @MockBean WorkerFromAuthentication workerFromAuthentication;

  Authentication authentication;
  Worker authenticatedWorker;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    authenticatedWorker =
        new Worker(
            "workerCode",
            "workerName",
            "email",
            "fullname",
            "address",
            "random city",
            "nif",
            "stat");
    workerRepository.save(authenticatedWorker);
    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    var product = new Product("product-code", "product-name", "product-description");
    productRepository.save(product);
    var mission1 = new Mission("mission1-code", "title1", "description1", 10, product);
    var mission2 = new Mission("mission2-code", "title2", "description2", 2, product);
    missionRepository.saveAll(List.of(mission1, mission2));
  }

  @Test
  void fetch_product_from_database() {
    var thProducts =
        productService.filterThProductByWorkerCodeAndDateBetween(null, null, null, true);

    assertFalse(thProducts.isEmpty());
  }

  @Test
  void filter_product_by_date_between() {
    var savedWorker = workerRepository.findByCode(authenticatedWorker.code());
    var thProducts =
        productService
            .filterThProductByWorkerCodeAndDateBetween(
                savedWorker.code(), "2024-06-01", "2025-06-01", true)
            .stream()
            .filter(product -> product.code().equals("product-code"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Test product not found"));

    var expected =
        new ThProduct(
            "product-code", "product-name", "product-description", thMissionList(), false);

    assertEquals(expected, thProducts);
  }

  private List<ThMission> thMissionList() {
    var mission1 =
        new ThMission("mission1-code", "title1", "description1", List.of(), false, false);
    var mission2 =
        new ThMission("mission2-code", "title2", "description2", List.of(), false, false);
    return List.of(mission2, mission1);
  }
}
