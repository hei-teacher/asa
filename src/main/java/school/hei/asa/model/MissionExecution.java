package school.hei.asa.model;

import java.time.LocalDate;
import java.util.Set;

public record MissionExecution(Mission mission, Worker worker, Set<LocalDate> dates) {}
