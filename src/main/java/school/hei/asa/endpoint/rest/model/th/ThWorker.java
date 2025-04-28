package school.hei.asa.endpoint.rest.model.th;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class ThWorker {
  String code;
  String name;
  String email;
  String workerType;
  Instant entranceInstant;
  String level;
  Instant levelEntranceInstant;
}
