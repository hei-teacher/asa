package school.hei.asa.repository.mapper;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Product;
import school.hei.asa.repository.model.JProduct;

@AllArgsConstructor
@Component
public class ProductMapper {

  private final MissionMapper missionMapper;

  public Product toDomain(JProduct jProduct) {
    return toDomain(jProduct, new HashMap<>());
  }

  /*package-private*/ Product toDomain(
      JProduct jProduct,
      // note(circular-mission-product-avoidance)
      Map<String, Product> productsByCode) {
    var code = jProduct.getCode();
    if (productsByCode.containsKey(code)) {
      return productsByCode.get(code);
    }

    var product = new Product(code, jProduct.getName(), jProduct.getDescription());
    productsByCode.put(code, product);
    jProduct.getMissions().stream()
        .map(
            jMission ->
                missionMapper.toDomain(jMission, new HashMap<>(), new HashMap<>(), productsByCode))
        .forEach(product::add);
    return product;
  }

  public JProduct toEntity(Product product) {
    return toEntity(product, new HashMap<>());
  }

  /*package-private*/ JProduct toEntity(
      Product product,
      // note(circular-jmission-jproduct-avoidance)
      Map<String, JProduct> jProductsByCode) {
    var code = product.code();
    if (jProductsByCode.containsKey(code)) {
      return jProductsByCode.get(code);
    }

    var jProduct = new JProduct();
    jProductsByCode.put(code, jProduct);
    jProduct.setCode(product.code());
    jProduct.setName(product.name());
    jProduct.setDescription(product.description());
    jProduct.setMissions(
        product.missions().stream()
            .map(mission -> missionMapper.toEntity(mission, jProductsByCode))
            .toList());
    return jProduct;
  }
}
