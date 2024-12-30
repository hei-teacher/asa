package school.hei.asa.repository;

import jakarta.transaction.Transactional;
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

  @Transactional
  public List<Worker> findAll() {
    return jWorkerRepository.findAll().stream().map(workerMapper::toDomain).toList();
  }

  @Transactional
  public Worker findByCode(String code) {
    return workerMapper.toDomain(jWorkerRepository.findByCode(code));
  }

  @Transactional
  public void save(Worker worker) {
    jWorkerRepository.save(workerMapper.toEntity(worker, List.of()));
  }

  @Transactional
  public void saveAll(List<Worker> workers) {
    workers.forEach(this::save);
  }
}
