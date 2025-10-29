package school.hei.asa.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import school.hei.asa.repository.model.JWorkerLevel;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class WorkerLevelHistory {
  private final Worker worker;
  private final JWorkerLevel level;
  private final Instant entranceInstant;
  private final String contractType;
  private final Integer projectedDaysToWork;
  private final BigDecimal compensation;
  private final String jobTitle;
  private final Integer contractDuration;
}
