package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Product;
import school.hei.asa.repository.jrepository.JProductRepository;
import school.hei.asa.repository.mapper.ProductMapper;
import school.hei.asa.repository.model.JProduct;

@AllArgsConstructor
@Repository
public class ProductRepository {

  private final JProductRepository jProductRepository;
  private final ProductMapper productMapper;

  @Transactional
  public void save(Product product) {
    jProductRepository.save(productMapper.toEntity(product));
  }

  @Transactional
  public List<Product> findAll() {
    return productMapper.toDomain(jProductRepository.findAll());
  }

  @Transactional
  public List<Product> findAllByDateRange(LocalDate startDate, LocalDate endDate) {
    List<JProduct> jProducts = jProductRepository.findAllDeepByDateRange(startDate, endDate);
    jProducts.forEach(jp -> jp.getMissions().size());
    return jProducts.stream().map(productMapper::toDomain).toList();
  }

  @Transactional
  public Product findByCode(String code) {
    return productMapper.toDomain(jProductRepository.findByCode(code));
  }
}
