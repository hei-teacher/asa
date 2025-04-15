package school.hei.asa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import school.hei.asa.repository.model.JWorkerLevelEnum;

import java.time.Instant;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "code")
public class WorkerLevelHistory {
  private final Worker worker;
  private final JWorkerLevelEnum level;
  private final Instant entranceInstant;
}
