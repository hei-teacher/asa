package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ProductTest {
  @Test
  void cannot_add_missions_with_different_products() {
    var product1 = new Product("product1", "my-product", "a cool product");
    var product2 = new Product("product2", "your-product", "a much cooler product");
    var mission = new Mission("mission1", "secret-mission", "a cool mission", 0, product1);

    var exception = assertThrows(IllegalArgumentException.class, () -> product2.add(mission));

    assertFalse(exception.getMessage().isEmpty());
  }

  @Test
  void get_product_maxDurationInDays() {
    var product = new Product("product1", "my-product", "a cool product");
    var mission1 = new Mission("mission1", "mission1", "a cool mission", 0, product);
    var mission2 = new Mission("mission2", "mission2", "a cool mission", 2, product);
    var mission3 = new Mission("mission3", "mission3", "a cool mission", 3, product);
    var mission4 = new Mission("mission4", "mission4", "a cool mission", 4, product);

    product.add(mission1);
    product.add(mission2);
    product.add(mission3);
    product.add(mission4);

    assertEquals(9, product.maxDurationInDays());
  }
}
