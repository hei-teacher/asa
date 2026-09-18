package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Worker;

class ContractServiceIT extends FacadeIT {
  @Autowired ContractService contractService;

  @Test
  void active_contract_without_end_instant_is_found() {
    var worker = studentWorker();

    var actual = contractService.findActiveContractByWorker(worker);

    assertTrue(actual.isPresent());
    assertEquals("L4P-2026", actual.get().level().code());
  }

  @Test
  void active_contract_without_end_instant_has_full_remaining_days() {
    // contract entrance=2025-01-01, duration=80 days, no matching mission_execution in that
    // range in the fixtures, so no day is worked yet
    var worker = studentWorker();

    var actual = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    assertEquals(80d, actual);
  }

  @Test
  void ended_contract_has_zero_remaining_days() {
    // contract entrance=2024-01-01, endInstant=2024-06-01 : endInstant is set, so it is not an
    // active contract (active = endInstant IS NULL)
    var worker = endedContractWorker();

    var actual = contractService.getRemainingDaysOnActiveContractOrZero(worker);

    assertEquals(0d, actual);
  }

  private Worker studentWorker() {
    return new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "", null, null);
  }

  private Worker endedContractWorker() {
    return new Worker("W-ENDED-01", "Ended Worker", "", "", "", "", "", "", null, null);
  }
}
