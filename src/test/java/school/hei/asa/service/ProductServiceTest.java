package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Product;
import school.hei.asa.repository.ProductRepository;

class ProductServiceTest {

  private final ProductRepository productRepository = mock(ProductRepository.class);
  private final ProductService productService = new ProductService(productRepository);

  @Test
  void getAllProducts() {
    var products =
        List.of(new Product("P1", "Product 1", "Desc 1"), new Product("P2", "Product 2", "Desc 2"));
    when(productRepository.findAll()).thenReturn(products);

    var result = productService.getAllProducts();

    assertEquals(2, result.size());
  }
}
