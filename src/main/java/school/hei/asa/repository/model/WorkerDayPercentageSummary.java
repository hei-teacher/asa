package school.hei.asa.repository.model;

import java.time.Instant;

public record WorkerDayPercentageSummary(
    String workerCode, Double totalDayPercentage, Instant creationInstant, String missionCode) {}
