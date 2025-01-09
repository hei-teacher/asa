package school.hei.asa.repository.mapper;

import java.util.HashMap;
import java.util.Map;

/*package-private*/ class Cache {
  private final Map<Class<?>, Map<String, Object>> memory = new HashMap<>();

  public void put(String id, Object o) {
    var clazz = o.getClass();
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
    return memory.get(clazz) == null ? null : (T) memory.get(clazz).get(id);
  }

  public <T> T getOrDefault(Class<T> clazz, String id, T oDefault) {
    return contains(clazz, id) ? get(clazz, id) : oDefault;
  }
}
