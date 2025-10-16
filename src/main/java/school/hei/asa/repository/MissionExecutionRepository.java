package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JMissionExecutionRepository;
import school.hei.asa.repository.mapper.MissionExecutionMapper;
import school.hei.asa.repository.mapper.WorkerMapper;
import school.hei.asa.repository.model.WorkerDayPercentageSummary;

@AllArgsConstructor
@Component
public class MissionExecutionRepository {
  private final JMissionExecutionRepository jMissionExecutionRepository;
  private final MissionExecutionMapper missionExecutionMapper;
  private final WorkerMapper workerMapper;

  @Transactional
  public List<MissionExecution> findAllBy(Worker worker, LocalDate date) {
    return missionExecutionsByDateBetween(worker, date, date);
  }

  @Transactional
  public List<MissionExecution> missionExecutionsByDateBetween(
      Worker worker, LocalDate startDate, LocalDate endDate) {
    var jmeList =
        jMissionExecutionRepository.findByWorkerCodeAndDateBetween(
            workerMapper.toEntity(worker).getCode(), startDate, endDate);
    return missionExecutionMapper.toDomain(jmeList);
  }

  @Transactional
  public List<WorkerDayPercentageSummary> dayPercentageSummary(
      Worker worker, Instant startDate, Instant endDate) {
    return jMissionExecutionRepository.findWorkerDayPercentageSummary(
        worker.code(), startDate, endDate);
  }
}
