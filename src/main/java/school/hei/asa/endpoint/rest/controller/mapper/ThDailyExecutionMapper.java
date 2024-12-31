package school.hei.asa.endpoint.rest.controller.mapper;

import static java.lang.Double.parseDouble;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;

@AllArgsConstructor
@Component
public class ThDailyExecutionMapper {

  private final MissionRepository missionRepository;

  public DailyExecution toDomain(ThDailyExecutionForm dmeForm, Worker worker) {
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

    List<MissionExecution> executions = new ArrayList<>();
    var date = LocalDate.parse(dmeForm.date());
    optionalAdd(executions, worker, date, mission1Opt, percentage1Opt);
    optionalAdd(executions, worker, date, mission2Opt, percentage2Opt);
    optionalAdd(executions, worker, date, mission3Opt, percentage3Opt);
    optionalAdd(executions, worker, date, mission4Opt, percentage4Opt);
    optionalAdd(executions, worker, date, mission5Opt, percentage5Opt);
    return new DailyExecution(worker, date, executions);
  }

  private void optionalAdd(
      List<MissionExecution> list,
      Worker worker,
      LocalDate date,
      Optional<Mission> keyOpt,
      Optional<Double> valueOpt) {
    if (keyOpt.isPresent() && valueOpt.isPresent()) {
      list.add(new MissionExecution(keyOpt.get(), worker, date, valueOpt.get()));
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
