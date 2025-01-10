package school.hei.asa.repository.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Due to bidirectional associations, circular dependency can occur when mapping.
 * To cut the circle, cache mapped objects so that they can be read from memory
 * instead of being recomputed.
 */
/*package-private*/ class Cache {
  private final Map<Class<?>, Map<String, Object>> memory = new HashMap<>();

  public void put(String id, Object o) {
    put(id, o, o.getClass());
  }

  public void put(String id, Object o, Class<?> clazz) {
    if (memory.containsKey(clazz)) {
      memory.get(clazz).put(id, o);
    } else {
      memory.put(clazz, new HashMap<>(Map.of(id, o)));
    }
  }

  public boolean contains(Class<?> clazz, String id) {
    return memory.containsKey(clazz) && memory.get(clazz).containsKey(id);
  }

  public <T> T get(Class<T> clazz, String id) {
    return (T) memory.get(clazz).get(id);
  }

  public <T> T getOrDefault(Class<T> clazz, String id, T oDefault) {
    return contains(clazz, id) ? get(clazz, id) : oDefault;
  }
}
