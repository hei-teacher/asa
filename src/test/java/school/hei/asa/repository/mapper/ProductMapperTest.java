package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.repository.model.JMission;
import school.hei.asa.repository.model.JProduct;

class ProductMapperTest {

  private final MissionMapper missionMapper = mock(MissionMapper.class);
  private final ProductMapper productMapper = new ProductMapper(missionMapper);

  @Test
  void toDomain() {
    var jProduct = new JProduct();
    jProduct.setCode("P1");
    jProduct.setName("Product 1");
    jProduct.setDescription("Desc");
    jProduct.setMissions(List.of());

    var result = productMapper.toDomain(jProduct);

    assertEquals("P1", result.code());
    assertEquals("Product 1", result.name());
    assertEquals(0, result.missions().size());
  }

  @Test
  void toDomain_with_missions() {
    var jMission = new JMission();
    jMission.setCode("M001");
    jMission.setTitle("M1");
    var jProduct = new JProduct();
    jProduct.setCode("P1");
    jProduct.setName("P1");
    jProduct.setDescription("Desc");
    jProduct.setMissions(List.of(jMission));

    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M001", "M1", "Desc", 10, product);
    when(missionMapper.toDomain(eq(jMission), any())).thenReturn(mission);

    var result = productMapper.toDomain(jProduct);

    assertEquals("P1", result.code());
    assertEquals(1, result.missions().size());
    assertEquals("M001", result.missions().stream().findFirst().get().code());
  }

  @Test
  void toEntity() {
    var product = new Product("P1", "P1", "Desc");

    var jMission = new JMission();
    when(missionMapper.toEntity(any(), any())).thenReturn(jMission);

    var result = productMapper.toEntity(product);

    assertEquals("P1", result.getCode());
  }

  @Test
  void toDomain_list() {
    var jProduct = new JProduct();
    jProduct.setCode("P1");
    jProduct.setName("P1");
    jProduct.setDescription("Desc");
    jProduct.setMissions(List.of());

    var result = productMapper.toDomain(List.of(jProduct));

    assertEquals(1, result.size());
  }
}
