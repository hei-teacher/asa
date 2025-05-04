package school.hei.asa.repository;

import static java.util.stream.Collectors.groupingBy;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
  public List<MissionExecution> findAllBy(Worker worker, LocalDate date) {
    return missionExecutionsByDate(worker).getOrDefault(date, List.of());
  }

  @Transactional
  public Map<LocalDate, List<MissionExecution>> missionExecutionsByDate(Worker worker) {
    var jmeList = jMissionExecutionRepository.findAllByWorker(workerMapper.toEntity(worker));
    var meList = missionExecutionMapper.toDomain(jmeList);
    return meList.stream().collect(groupingBy(MissionExecution::date));
  }

  @Transactional
  public List<JMissionExecution> missionExecutionsByDateBetween(
      Worker worker, LocalDate startDate, LocalDate endDate) {
    return jMissionExecutionRepository.findByWorkerCodeAndDateBetween(
        workerMapper.toEntity(worker).getCode(), startDate, endDate);
  }
}
