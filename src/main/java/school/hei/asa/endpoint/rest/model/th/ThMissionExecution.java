package school.hei.asa.endpoint.rest.model.th;

import java.util.List;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;

public record ThMissionExecution(Mission mission, List<MissionExecution> executions) {}
