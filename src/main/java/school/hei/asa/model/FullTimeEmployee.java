package school.hei.asa.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public final class FullTimeEmployee extends Worker {

  public FullTimeEmployee(UUID id, String name) {
    super(id, name);
  }

  @Override
  public Set<LocalDate> availabilities() {
    throw new RuntimeException("TODO");
  }
}
