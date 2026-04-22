package school.hei.asa.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.Product;
import school.hei.asa.repository.ProductRepository;

@Service
@AllArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;

  @Transactional
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Transactional
  public List<Product> getProductsByDate(LocalDate startDate, LocalDate endDate) {
    return productRepository.findAllByDateRange(startDate, endDate);
  }
}
