package school.hei.asa.repository.model;

import java.time.Instant;

public interface WorkerDayPercentageSummary {
  String getWorkerCode();

  Double getTotalDayPercentage();

  Instant getCreationInstant();

  String getMissionCode();
}
