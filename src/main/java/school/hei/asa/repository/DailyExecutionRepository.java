package school.hei.asa.repository;

import static java.util.stream.Collectors.groupingBy;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JMissionExecutionRepository;
import school.hei.asa.repository.mapper.MissionExecutionMapper;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Repository
public class DailyExecutionRepository {

  private final JMissionExecutionRepository jMissionExecutionRepository;
  private final MissionExecutionMapper missionExecutionMapper;
  private final MissionExecutionRepository missionExecutionRepository;

  @Transactional
  public void save(DailyExecution dailyExecution) {
    var toSave = new ArrayList<JMissionExecution>();
    dailyExecution
        .executions()
        .forEach(missionExecution -> toSave.add(missionExecutionMapper.toEntity(missionExecution)));
    jMissionExecutionRepository.saveAll(toSave);
  }

  @Transactional
  public List<DailyExecution> findAll() {
    var meListByDate =
        jMissionExecutionRepository.findAll().stream()
            .map(missionExecutionMapper::toDomain)
            .collect(groupingBy(MissionExecution::date));
    List<DailyExecution> dailyExecutions = new ArrayList<>();
    meListByDate.forEach(
        (date, meListOfDate) ->
            dailyExecutions.add(
                new DailyExecution(meListOfDate.get(0).worker(), date, meListOfDate)));
    return dailyExecutions;
  }

  @Transactional
  public List<DailyExecution> findAllBy(Worker worker) {
    var meListByDate = missionExecutionRepository.missionExecutionsBy(worker);
    List<DailyExecution> dailyExecutions = new ArrayList<>();
    meListByDate.forEach(
        (date, meListOfDate) ->
            dailyExecutions.add(new DailyExecution(worker, date, meListOfDate)));
    return dailyExecutions;
  }
}
