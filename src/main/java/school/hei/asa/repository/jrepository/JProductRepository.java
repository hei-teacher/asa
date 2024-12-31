package school.hei.asa.repository.jrepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.asa.repository.model.JProduct;

public interface JProductRepository extends JpaRepository<JProduct, String> {
  @Override
  List<JProduct> findAll();

  JProduct findByCode(String code);
}
