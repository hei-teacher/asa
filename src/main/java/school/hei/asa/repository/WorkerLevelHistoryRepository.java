package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.jrepository.JWorkerLevelHistoryRepository;
import school.hei.asa.repository.mapper.WorkerLevelHistoryMapper;
import school.hei.asa.repository.mapper.WorkerMapper;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class WorkerLevelHistoryRepository {

  private final JWorkerLevelHistoryRepository jWorkerLevelHistoryRepository;
  private final WorkerLevelHistoryMapper workerLevelHistoryMapper;
  private final WorkerMapper workerMapper;

  @Transactional
  public List<WorkerLevelHistory> findAllByWorker(Worker worker) {
    return workerLevelHistoryMapper.toDomain(jWorkerLevelHistoryRepository.findAllByWorker(workerMapper.toEntity(worker)));
  }
}
