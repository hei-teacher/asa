package school.hei.asa.endpoint.rest.model.th;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import school.hei.asa.repository.model.JWorkerLevelEnum;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class ThWorker {
  String code;
  String name;
  String email;
  Instant entranceInstant;
  JWorkerLevelEnum level;
  Instant levelEntranceInstant;
}
