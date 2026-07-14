package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Product;
import school.hei.asa.repository.ProductRepository;

class ProductServiceConcreteTest {

  private final ProductRepository productRepository = mock(ProductRepository.class);
  private final ProductService service = new ProductService(productRepository);

  @Test
  void getAllProducts_delegates() {
    when(productRepository.findAll()).thenReturn(List.of(new Product("P1", "Product", "Desc")));

    var result = service.getAllProducts();

    assertEquals(1, result.size());
    assertEquals("P1", result.getFirst().code());
  }
}
