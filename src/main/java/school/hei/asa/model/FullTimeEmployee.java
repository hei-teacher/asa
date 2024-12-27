package school.hei.asa.model;

import java.time.LocalDate;
import java.util.Set;

public final class FullTimeEmployee extends Worker {

  public FullTimeEmployee(String code, String name) {
    super(code, name);
  }

  @Override
  public Set<LocalDate> availabilities() {
    throw new RuntimeException("TODO");
  }
}
