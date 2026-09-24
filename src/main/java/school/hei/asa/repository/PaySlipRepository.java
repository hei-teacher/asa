package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.time.YearMonth;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.PaySlipForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JPaySlipRepository;
import school.hei.asa.repository.mapper.PaySlipMapper;
import school.hei.asa.repository.model.JPaySlip;

@AllArgsConstructor
@Repository
public class PaySlipRepository {
  private final JPaySlipRepository jPaySlipRepository;
  private final PaySlipMapper paySlipMapper;

  @Transactional
  public void save(PaySlipForm paySlipForm, Worker worker) {
    jPaySlipRepository.save(paySlipMapper.toEntity(paySlipForm, worker));
  }

  @Transactional
  public Optional<Double> findLeftPaidLeave(Worker worker, YearMonth yearMonth) {
    return jPaySlipRepository
        .findByWorkerCodeAndYearMonth(worker.code(), yearMonth.toString())
        .map(JPaySlip::getLeftPaidLeave);
  }
}
