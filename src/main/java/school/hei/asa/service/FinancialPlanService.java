package school.hei.asa.service;

import static gen.patrimoine.modele.Devise.MGA;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.modele.Argent;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.FinancialPlan;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.cas.ContractsToCasSet;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

@AllArgsConstructor
@Service
public class FinancialPlanService {

  private final ContractRepository contractRepository;
  private final InvoiceService invoiceService;
  private final WorkerRepository workerRepository;

  @Transactional
  public FinancialPlan financialPlan(int year) {
    var contracts = contractRepository.findByYear(year);
    var contractsToCasSet = new ContractsToCasSet();
    contractsToCasSet.apply(new HashSet<>(contracts));
    return new FinancialPlan(
        mapOfCosts(year, contractsToCasSet.getCompanyCas()),
        mapOfExecuted(year),
        contractsToCasSet.getKoContracts());
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

  private Map<Month, Argent> mapOfExecuted(int year) {
    var map = new HashMap<Month, Argent>();
    var workers = workerRepository.findAll();

    for (Month m : Month.values()) {
      var amount =
          extractInvoiceDataByWorkers(workers, YearMonth.of(year, m)).stream()
              .map(InvoiceForm::amount)
              .reduce(BigDecimal::add)
              .get();
      map.put(
          m,
          amount.equals(BigDecimal.ZERO)
              ? new Argent(0, MGA)
              : new Argent(amount.doubleValue(), MGA).mult(-1));
    }
    return map;
  }

  public List<InvoiceForm> extractInvoiceDataByWorkers(List<Worker> workers, YearMonth yearMonth) {
    var invoiceForm =
        new InvoiceForm(
            UUID.randomUUID().toString(),
            yearMonth,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    return workers.stream()
        .map(worker -> invoiceService.extractInvoiceData(worker, invoiceForm))
        .toList();
  }
}
