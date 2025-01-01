package school.hei.asa.repository;

import static java.util.stream.Collectors.groupingBy;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JMissionExecutionRepository;
import school.hei.asa.repository.mapper.MissionExecutionMapper;
import school.hei.asa.repository.mapper.WorkerMapper;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Component
public class MissionExecutionRepository {
  private final JMissionExecutionRepository jMissionExecutionRepository;
  private final MissionExecutionMapper missionExecutionMapper;
  private final WorkerMapper workerMapper;

  @Transactional
  public void save(DailyExecution dailyExecution) {
    var toSave = new ArrayList<JMissionExecution>();
    dailyExecution
        .executions()
        .forEach(missionExecution -> toSave.add(missionExecutionMapper.toEntity(missionExecution)));
    jMissionExecutionRepository.saveAll(toSave);
  }

  @Transactional
  public List<DailyExecution> findAllDailyExecution() {
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
  public List<DailyExecution> findAllDailyExecutionBy(Worker worker) {
    var meListByDate = missionExecutionsBy(worker);
    List<DailyExecution> dailyExecutions = new ArrayList<>();
    meListByDate.forEach(
        (date, meListOfDate) ->
            dailyExecutions.add(new DailyExecution(worker, date, meListOfDate)));
    return dailyExecutions;
  }

  @Transactional
  public List<MissionExecution> findAllBy(Worker worker, LocalDate date) {
    return missionExecutionsBy(worker).getOrDefault(date, List.of());
  }

  private Map<LocalDate, List<MissionExecution>> missionExecutionsBy(Worker worker) {
    var meList =
        jMissionExecutionRepository
            .findAllByWorker(workerMapper.toEntity(worker, List.of()))
            .stream()
            .map(missionExecutionMapper::toDomain)
            .toList();
    return meList.stream().collect(groupingBy(MissionExecution::date));
  }
}
