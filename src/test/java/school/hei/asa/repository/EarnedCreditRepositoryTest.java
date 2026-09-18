package school.hei.asa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Worker;

class EarnedCreditRepositoryTest extends FacadeIT {
  @Autowired EarnedCreditRepository earnedCreditRepository;

  @Test
  void find_all_by_worker_and_year_month_returns_the_earned_credit() {
    var worker = new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");

    var actual = earnedCreditRepository.findAllByWorkerAndYearMonth(worker, YearMonth.of(2026, 1));

    assertEquals(1, actual.size());
    assertEquals("ABSENCE", actual.getFirst().getCredit().getCreditCode());
  }

  @Test
  void find_all_by_worker_and_year_month_returns_empty_when_nothing_earned() {
    var worker = new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");

    var actual = earnedCreditRepository.findAllByWorkerAndYearMonth(worker, YearMonth.of(2020, 1));

    assertEquals(0, actual.size());
  }
}
