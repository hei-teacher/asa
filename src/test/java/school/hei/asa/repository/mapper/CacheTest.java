package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CacheTest {

  @Test
  void put_and_get() {
    var cache = new Cache();
    cache.put("id1", "value1");
    assertEquals("value1", cache.get(String.class, "id1"));
  }

  @Test
  void contains_returns_true_when_present() {
    var cache = new Cache();
    cache.put("id1", "value1");
    assertTrue(cache.contains(String.class, "id1"));
  }

  @Test
  void contains_returns_false_when_absent() {
    var cache = new Cache();
    assertFalse(cache.contains(String.class, "nonexistent"));
  }

  @Test
  void contains_returns_false_for_different_class() {
    var cache = new Cache();
    cache.put("id1", "value1");
    assertFalse(cache.contains(Integer.class, "id1"));
  }

  @Test
  void getOrDefault_returns_default_when_not_found() {
    var cache = new Cache();
    var result = cache.getOrDefault(String.class, "id1", "default");
    assertEquals("default", result);
  }

  @Test
  void getOrDefault_returns_cached_when_found() {
    var cache = new Cache();
    cache.put("id1", "cached");
    var result = cache.getOrDefault(String.class, "id1", "default");
    assertEquals("cached", result);
  }

  @Test
  void put_with_explicit_class() {
    var cache = new Cache();
    cache.put("id1", 123, Integer.class);
    assertEquals(123, cache.get(Integer.class, "id1"));
  }

  @Test
  void multiple_ids_for_same_class() {
    var cache = new Cache();
    cache.put("a", "valueA");
    cache.put("b", "valueB");
    assertEquals("valueA", cache.get(String.class, "a"));
    assertEquals("valueB", cache.get(String.class, "b"));
  }
}
