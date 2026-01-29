package school.hei.asa.model.contract;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;

public record Contract(
    Worker worker,
    String jobTitle,
    ContractLevel level,
    Instant entranceInstant,
    Duration duration,
    List<DailyExecution> executions,
    String contractBucketKey) {}
