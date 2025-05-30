package school.hei.asa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "code")
public abstract sealed class Worker permits Contractor, FullTimeEmployee {
  private final String code;
  private final String name;
  private final String email;
  private final String fullname;
  private final String address;
  private final String city;
  private final String nif;
  private final String stat;
}
