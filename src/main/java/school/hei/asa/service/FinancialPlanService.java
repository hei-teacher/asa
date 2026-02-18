package school.hei.asa.service;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.modele.Argent;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.FinancialPlan;
import school.hei.asa.model.contract.cas.ContractsToCasSet;
import school.hei.asa.repository.ContractRepository;

@AllArgsConstructor
@Service
public class FinancialPlanService {

  private final ContractRepository contractRepository;

  @Transactional
  public FinancialPlan financialPlan(int year) {
    var contracts = contractRepository.findByYear(year);
    var contractsToCasSet = new ContractsToCasSet();
    contractsToCasSet.apply(new HashSet<>(contracts));
    return new FinancialPlan(
        mapOfCosts(year, contractsToCasSet.getCompanyCas()), contractsToCasSet.getKoContracts());
  }

  private Map<Month, Argent> mapOfCosts(int year, Cas companyCas) {
    var map = new HashMap<Month, Argent>();

    var patrimoine = companyCas.patrimoine();
    for (int m = 1; m <= 12; m++) {
      var startDate = LocalDate.of(year, m, 1);
      var endDate = startDate.plusMonths(1);
      var patrimoineAtEnd = patrimoine.projectionFuture(endDate);
      var patrimoineAtStart = patrimoine.projectionFuture(startDate);
      map.put(
          Month.of(m),
          patrimoineAtEnd
              .getValeurComptable()
              .minus(patrimoineAtStart.getValeurComptable(), endDate));
    }

    return map;
  }
}
