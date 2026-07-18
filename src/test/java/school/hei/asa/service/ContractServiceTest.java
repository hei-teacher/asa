package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

class ContractServiceTest {

  private final ContractService contractService =
      new ContractService(mock(WorkerRepository.class), mock(ContractRepository.class), 10);

  @Test
  void is_below_threshold_returns_true_when_remaining_days_under_threshold() {
    assertTrue(contractService.isBelowThreshold(5));
  }

  @Test
  void is_below_threshold_returns_false_when_remaining_days_at_or_above_threshold() {
    assertFalse(contractService.isBelowThreshold(10));
    assertFalse(contractService.isBelowThreshold(15));
  }
}
