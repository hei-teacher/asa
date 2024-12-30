package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Product;
import school.hei.asa.repository.jrepository.JProductRepository;
import school.hei.asa.repository.mapper.ProductMapper;

@AllArgsConstructor
@Repository
public class ProductRepository {

  private final JProductRepository jProductRepository;
  private final ProductMapper productMapper;

  @Transactional
  public void save(Product product) {
    jProductRepository.save(productMapper.toEntity(product));
  }
}
