package school.hei.asa.repository.mapper;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.Product;
import school.hei.asa.repository.model.JProduct;

@AllArgsConstructor
@Component
public class ProductMapper {

  private final MissionMapper missionMapper;

  public Product toDomain(JProduct jProduct) {
    return toDomain(jProduct, new Cache());
  }

  public List<Product> toDomain(List<JProduct> jProducts) {
    var cache = new Cache();
    return jProducts.stream().map(jProduct -> toDomain(jProduct, cache)).toList();
  }

  /*package-private*/ Product toDomain(JProduct jProduct, Cache cache) {
    var code = jProduct.getCode();
    if (cache.contains(Product.class, code)) {
      return cache.get(Product.class, code);
    }

    var product = new Product(code, jProduct.getName(), jProduct.getDescription());
    cache.put(code, product);
    jProduct.getMissions().stream()
        .map(jMission -> missionMapper.toDomain(jMission, cache))
        .forEach(product::add);
    return product;
  }

  public JProduct toEntity(Product product) {
    return toEntity(product, new Cache());
  }

  /*package-private*/ JProduct toEntity(Product product, Cache cache) {
    var code = product.code();
    if (cache.contains(JProduct.class, code)) {
      return cache.get(JProduct.class, code);
    }

    var jProduct = new JProduct();
    cache.put(code, jProduct);
    jProduct.setCode(product.code());
    jProduct.setName(product.name());
    jProduct.setDescription(product.description());
    jProduct.setMissions(
        product.missions().stream()
            .map(mission -> missionMapper.toEntity(mission, cache))
            .toList());
    return jProduct;
  }
}
