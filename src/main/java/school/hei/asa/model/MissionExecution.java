package school.hei.asa.model;

import java.time.Instant;
import java.time.LocalDate;

public record MissionExecution(
    Mission mission,
    Worker worker,
    LocalDate date,
    double dayPercentage,
    String comment,
    Instant reportedAt) {}
