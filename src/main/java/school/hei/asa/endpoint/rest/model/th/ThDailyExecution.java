package school.hei.asa.endpoint.rest.model.th;

import java.time.LocalDate;
import java.util.List;

public record ThDailyExecution(LocalDate date, List<ThMissionExecution> thExecutions) {}
