package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.EarnedCredit;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JEarnedCreditsRepository;
import school.hei.asa.repository.mapper.EarnedCreditsMapper;
import school.hei.asa.repository.mapper.WorkerMapper;

@AllArgsConstructor
@Repository
public class EarnedCreditRepository {

  private final JEarnedCreditsRepository jEarnedCreditsRepository;
  private final EarnedCreditsMapper earnedCreditsMapper;
  private final WorkerMapper workerMapper;

  @Transactional
  public List<EarnedCredit> findAllByWorkerAndYearMonth(Worker worker, YearMonth yearMonth) {
    return jEarnedCreditsRepository
        .findAllByWorkerAndYearMonth(workerMapper.toEntity(worker), yearMonth.toString())
        .stream()
        .map(earnedCreditsMapper::toDomain)
        .toList();
  }
}
