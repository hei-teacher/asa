package school.hei.asa.model;

import java.time.LocalDate;
import java.util.Set;

public sealed class Contractor extends Worker permits StudentContractor, PartnerContractor {
  public Contractor(String code, String name) {
    super(code, name);
  }

  @Override
  public Set<LocalDate> availabilities() {
    throw new RuntimeException("TODO");
  }
}
