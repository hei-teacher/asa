package school.hei.asa.service;

import static gen.patrimoine.modele.Devise.MGA;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import gen.patrimoine.cas.Cas;
import gen.patrimoine.modele.Argent;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.model.BankAccount;
import school.hei.asa.model.FinancialPlan;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.cas.ContractsToCasSet;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerRepository;

@AllArgsConstructor
@Service
public class FinancialPlanService {

  private final ContractRepository contractRepository;
  private final InvoiceService invoiceService;
  private final WorkerRepository workerRepository;
  private final BankAccountRepository bankAccountRepository;
  private final MissionExecutionRepository missionExecutionRepository;

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

    var allContracts = contractRepository.findAll();
    var contractsByWorker = allContracts.stream().collect(groupingBy(Contract::worker));

    var allBankAccounts = bankAccountRepository.findAll();
    var bankAccountByWorkerCode =
        allBankAccounts.stream()
            .collect(toMap(BankAccount::worker, Function.identity(), (a, b) -> a));

    var yearStart = LocalDate.of(year, 1, 1);
    var yearEnd = LocalDate.of(year, 12, 31);
    var allMissionExecutions =
        missionExecutionRepository.missionExecutionsByDateBetweenAllWorkers(yearStart, yearEnd);
    var missionExecutionsByWorker =
        allMissionExecutions.stream().collect(groupingBy(MissionExecution::worker));

    for (Month m : Month.values()) {
      var yearMonth = YearMonth.of(year, m);
      var amount =
          workers.stream()
              .map(
                  worker -> {
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
                    var workerContracts =
                        contractsByWorker.getOrDefault(worker, List.of()).stream()
                            .sorted(comparing(Contract::entranceInstant, Comparator.reverseOrder()))
                            .toList();
                    var workerMissionExecutions =
                        missionExecutionsByWorker.getOrDefault(worker, List.of());
                    var bankAccount = bankAccountByWorkerCode.get(worker);
                    return invoiceService.extractInvoiceDataBatched(
                        invoiceForm, workerContracts, workerMissionExecutions, bankAccount);
                  })
              .map(InvoiceForm::amount)
              .map(a -> a == null ? BigDecimal.ZERO : a)
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
}
