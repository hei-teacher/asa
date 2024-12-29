package school.hei.asa.repository;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JWorkerRepository;
import school.hei.asa.repository.mapper.WorkerMapper;

@AllArgsConstructor
@Repository
public class WorkerRepository {

  private final JWorkerRepository jWorkerRepository;
  private final WorkerMapper workerMapper;

  public List<Worker> findAll() {
    return jWorkerRepository.findAll().stream().map(workerMapper::toDomain).toList();
  }
}
