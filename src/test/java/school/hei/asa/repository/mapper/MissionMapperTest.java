package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.repository.model.JMission;
import school.hei.asa.repository.model.JProduct;

class MissionMapperTest {

  private final ProductMapper productMapper = mock(ProductMapper.class);
  private final MissionExecutionMapper missionExecutionMapper = mock(MissionExecutionMapper.class);
  private final MissionMapper missionMapper =
      new MissionMapper(productMapper, missionExecutionMapper);

  @Test
  void toDomain() {
    var jProduct = new JProduct();
    jProduct.setCode("P1");
    var jMission = new JMission();
    jMission.setCode("M001");
    jMission.setTitle("Mission1");
    jMission.setDescription("Desc");
    jMission.setMaxDurationInDays(10);
    jMission.setProduct(jProduct);

    var product = new Product("P1", "P1", "Desc");
    when(productMapper.toDomain(eq(jProduct), any())).thenReturn(product);

    var result = missionMapper.toDomain(jMission);

    assertEquals("M001", result.code());
    assertEquals("Mission1", result.title());
    assertEquals(10, result.maxDurationInDays());
  }

  @Test
  void toEntity() {
    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M001", "Mission1", "Desc", 10, product);

    var jProduct = new JProduct();
    when(productMapper.toEntity(any(), any())).thenReturn(jProduct);

    var result = missionMapper.toEntity(mission);

    assertEquals("M001", result.getCode());
    assertEquals("Mission1", result.getTitle());
  }

  @Test
  void toDomain_with_cache_returns_cached() {
    var jProduct = new JProduct();
    jProduct.setCode("P1");
    var jMission = new JMission();
    jMission.setCode("M001");
    jMission.setTitle("Mission1");
    jMission.setDescription("Desc");
    jMission.setMaxDurationInDays(10);
    jMission.setProduct(jProduct);

    var product = new Product("P1", "P1", "Desc");
    when(productMapper.toDomain(eq(jProduct), any())).thenReturn(product);

    var cache = new Cache();
    var first = missionMapper.toDomain(jMission, cache);
    var second = missionMapper.toDomain(jMission, cache);

    assertEquals(first, second);
  }
}
