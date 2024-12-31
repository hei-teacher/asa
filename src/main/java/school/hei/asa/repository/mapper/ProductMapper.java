package school.hei.asa.repository.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Product;
import school.hei.asa.repository.model.JProduct;

@AllArgsConstructor
@Component
public class ProductMapper {

  public Product toDomain(JProduct product) {
    return new Product(product.getCode(), product.getName(), product.getDescription());
  }

  public JProduct toEntity(Product product) {
    var jProduct = new JProduct();
    jProduct.setCode(product.code());
    jProduct.setName(product.name());
    jProduct.setDescription(product.description());
    return jProduct;
  }
}
