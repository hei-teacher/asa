package school.hei.asa.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@ToString
@Builder
@Jacksonized
@EqualsAndHashCode(of = "code")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Worker {
  private final String code;
  private final String name;
  private final String email;
  private final String fullname;
  private final String address;
  private final String city;
  private final String nif;
  private final String stat;
}
