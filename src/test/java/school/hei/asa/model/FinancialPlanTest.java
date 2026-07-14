package school.hei.asa.model;

import static org.junit.jupiter.api.Assertions.*;

import gen.patrimoine.modele.Argent;
import java.time.Duration;
import java.time.Instant;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;

class FinancialPlanTest {

  @Test
  void toString_contains_planned_and_executed_costs() {
    var plannedCost = new HashMap<Month, Argent>();
    var executedCost = new HashMap<Month, Argent>();
    for (int i = 1; i <= 12; i++) {
      plannedCost.put(Month.of(i), Argent.euro(1000.0));
      executedCost.put(Month.of(i), Argent.euro(800.0));
    }
    var financialPlan = new FinancialPlan(plannedCost, executedCost, Map.of());

    var result = financialPlan.toString();

    assertTrue(result.contains("plannedCost"));
    assertTrue(result.contains("executedCost"));
    assertTrue(result.contains("koContracts"));
  }

  @Test
  void toString_with_ko_contracts() {
    var plannedCost = new HashMap<Month, Argent>();
    var executedCost = new HashMap<Month, Argent>();
    for (int i = 1; i <= 12; i++) {
      plannedCost.put(Month.of(i), Argent.euro(1000.0));
      executedCost.put(Month.of(i), Argent.euro(800.0));
    }
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var contractLevel = new ContractLevel("L1", ContractType.fullTimeEmployee, 5000.0, null);
    var contract =
        new Contract(
            worker,
            "Dev",
            contractLevel,
            Instant.now(),
            null,
            Duration.ofDays(365),
            "Company",
            "key");
    Map<Contract, Exception> koContracts = new HashMap<>();
    koContracts.put(contract, new RuntimeException("Test error"));

    var financialPlan = new FinancialPlan(plannedCost, executedCost, koContracts);

    var result = financialPlan.toString();

    assertTrue(result.contains("John"));
    assertTrue(result.contains("Test error"));
  }
}
