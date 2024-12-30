package school.hei.asa.endpoint.rest.controller.mapper;

import static java.lang.Double.parseDouble;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.DailyMissionExecutionForm;
import school.hei.asa.model.DailyMissionExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;

@AllArgsConstructor
@Component
public class MissionExecutionMapper {

  private final MissionRepository missionRepository;

  public DailyMissionExecution toDomain(DailyMissionExecutionForm dmeForm, Worker worker) {
    Optional<Mission> mission1Opt = findMissionByOptionalCode(dmeForm.missionCode1());
    var mission2Opt = findMissionByOptionalCode(dmeForm.missionCode2());
    var mission3Opt = findMissionByOptionalCode(dmeForm.missionCode3());
    var mission4Opt = findMissionByOptionalCode(dmeForm.missionCode4());
    var mission5Opt = findMissionByOptionalCode(dmeForm.missionCode5());

    Optional<Double> percentage1Opt = optionalPercentage(dmeForm.missionPercentage1());
    var percentage2Opt = optionalPercentage(dmeForm.missionPercentage2());
    var percentage3Opt = optionalPercentage(dmeForm.missionPercentage3());
    var percentage4Opt = optionalPercentage(dmeForm.missionPercentage4());
    var percentage5Opt = optionalPercentage(dmeForm.missionPercentage5());

    Map<Mission, Double> percentageByMission = new HashMap<>();
    optionalPut(percentageByMission, mission1Opt, percentage1Opt);
    optionalPut(percentageByMission, mission2Opt, percentage2Opt);
    optionalPut(percentageByMission, mission3Opt, percentage3Opt);
    optionalPut(percentageByMission, mission4Opt, percentage4Opt);
    optionalPut(percentageByMission, mission5Opt, percentage5Opt);
    return new DailyMissionExecution(worker, LocalDate.parse(dmeForm.date()), percentageByMission);
  }

  private void optionalPut(
      Map<Mission, Double> map, Optional<Mission> keyOpt, Optional<Double> valueOpt) {
    if (keyOpt.isPresent() && valueOpt.isPresent()) {
      map.put(keyOpt.get(), valueOpt.get());
    }
  }

  private Optional<Mission> findMissionByOptionalCode(String code) {
    return code == null || code.isBlank() ? Optional.empty() : missionRepository.findByCode(code);
  }

  private Optional<Double> optionalPercentage(String percentageStr) {
    return percentageStr == null || percentageStr.isBlank()
        ? Optional.empty()
        : Optional.of(parseDouble(percentageStr));
  }
}
