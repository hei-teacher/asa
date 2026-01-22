package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;

public class ProductServiceIT extends FacadeIT {
  @Autowired ProductService productService;

  @Test
  void fetch_all_products() {
    var products = productService.getAllProducts();

    assertFalse(products.isEmpty());
  }
}
