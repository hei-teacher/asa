package school.hei.asa.repository.mapper;

import java.time.YearMonth;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.EarnedCredit;
import school.hei.asa.repository.model.JEarnedCredits;

@AllArgsConstructor
@Component
public class EarnedCreditsMapper {

  private final CreditMapper creditMapper;
  private final WorkerMapper workerMapper;

  public EarnedCredit toDomain(JEarnedCredits jEarnedCredits) {
    return new EarnedCredit(
        jEarnedCredits.getId(),
        creditMapper.toDomain(jEarnedCredits.getCredit()),
        jEarnedCredits.getNumberOfUnits() == null
            ? null
            : jEarnedCredits.getNumberOfUnits().doubleValue(),
        YearMonth.parse(jEarnedCredits.getYearMonth()),
        jEarnedCredits.getWorker() == null
            ? null
            : workerMapper.toDomain(jEarnedCredits.getWorker()));
  }
}
