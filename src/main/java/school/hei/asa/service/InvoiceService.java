package school.hei.asa.service;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.UUID.randomUUID;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.InvoiceReferenceRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerLevelHistoryRepository;
import school.hei.asa.service.utils.NumberConverter;
import school.hei.asa.service.utils.NumberParser;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceService {
  private final NumberConverter numberConverter;
  private final NumberParser numberParser;
  private final WorkerLevelHistoryRepository workerLevelHistoryRepository;
  private final MissionExecutionRepository missionExecutionRepository;
  private final BankAccountRepository bankAccountRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final InvoiceReferenceRepository invoiceReferenceRepository;

  public Optional<InvoiceReference> findInvoiceReference(Worker worker, YearMonth yearMonth) {
    var invoiceReferenceList = invoiceReferenceRepository.findInvoiceReferenceByWorker(worker);
    log.info("here is the invoice result: {}", invoiceReferenceList);
    return invoiceReferenceList.stream()
        .filter(invoiceReference -> invoiceReference.yearMonth().equals(yearMonth))
        .findFirst();
  }

  public InvoiceForm extractInvoiceData(Worker worker, InvoiceForm invoiceForm) {
    var isEmpty = invoiceForm.yearMonth() == null;
    var workerLevelHistories = workerLevelHistoryRepository.findAllByWorker(worker);
    var hasLevelHistory = !workerLevelHistories.isEmpty();
    var referenceDate = now();
    var issueDate = referenceDate.plusDays(3);
    var yearMonth = isEmpty ? YearMonth.from(referenceDate) : invoiceForm.yearMonth();
    var firstCurrentMonthDay = yearMonth.atDay(1);
    var lastCurrentMonthDay = yearMonth.atEndOfMonth();
    var hasUpgradedLevel =
        hasLevelHistory
            && LocalDate.ofInstant(workerLevelHistories.getFirst().entranceInstant(), UTC)
                .isBefore(lastCurrentMonthDay)
            && LocalDate.ofInstant(workerLevelHistories.getFirst().entranceInstant(), UTC)
                .isAfter(firstCurrentMonthDay);
    var bankAccount = bankAccountRepository.findByWorkerCode(worker.code());
    if (hasUpgradedLevel) {
      var firstWorkerLevelHistory = workerLevelHistories.getFirst();
      var secondWorkerLevelHistory = workerLevelHistories.get(1);
      var firstTotalDaysWorked =
          missionExecutionPercentageSumByWorker(
              worker,
              firstCurrentMonthDay,
              LocalDate.ofInstant(firstWorkerLevelHistory.entranceInstant(), UTC));
      var secondTotalDaysWorked =
          missionExecutionPercentageSumByWorker(
              worker,
              LocalDate.ofInstant(firstWorkerLevelHistory.entranceInstant(), UTC),
              lastCurrentMonthDay);
      var firstInvoiceForm = generateInvoiceFormFrom(firstTotalDaysWorked, firstWorkerLevelHistory);
      var secondInvoiceForm =
          generateInvoiceFormFrom(secondTotalDaysWorked, secondWorkerLevelHistory);
      var total = firstInvoiceForm.amount().add(secondInvoiceForm.amount());
      var parsedTotal = numberConverter.convertToWords(numberParser.parseToNumber(total));
      return new InvoiceForm(
          yearMonth,
          referenceDate,
          issueDate,
          firstInvoiceForm.description(),
          firstInvoiceForm.quantity(),
          firstInvoiceForm.unitPrice(),
          firstInvoiceForm.amount(),
          true,
          secondInvoiceForm.description(),
          secondInvoiceForm.quantity(),
          secondInvoiceForm.unitPrice(),
          secondInvoiceForm.amount(),
          total,
          parsedTotal,
          bankAccount.toString());
    }
    var totalDaysWorked =
        missionExecutionPercentageSumByWorker(worker, firstCurrentMonthDay, lastCurrentMonthDay);
    var workerLevelhistory = hasLevelHistory ? workerLevelHistories.getFirst() : null;
    var tempResult = generateInvoiceFormFrom(totalDaysWorked, workerLevelhistory);
    return new InvoiceForm(
        yearMonth,
        referenceDate,
        issueDate,
        tempResult.description(),
        tempResult.quantity(),
        tempResult.unitPrice(),
        tempResult.amount(),
        false,
        null,
        null,
        null,
        null,
        tempResult.total(),
        tempResult.parsedAmount(),
        bankAccount.toString());
  }

  private InvoiceForm generateInvoiceFormFrom(
      Double totalDaysWorked, WorkerLevelHistory workerLevelHistory) {
    if (workerLevelHistory == null) {
      return new InvoiceForm(
          null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
    var unitPrice = workerLevelHistory.compensation();
    var amount = unitPrice.multiply(valueOf(totalDaysWorked));
    var parsedAmount = numberConverter.convertToWords(numberParser.parseToNumber(amount));
    var description = workerLevelHistory.jobTitle();

    return new InvoiceForm(
        null,
        null,
        null,
        description,
        totalDaysWorked,
        unitPrice,
        amount,
        null,
        null,
        null,
        null,
        null,
        amount,
        parsedAmount,
        null);
  }

  private Double missionExecutionPercentageSumByWorker(
      Worker worker, LocalDate startDate, LocalDate endDate) {
    return missionExecutionRepository
        .missionExecutionsByDateBetween(worker, startDate, endDate)
        .stream()
        .filter(me -> !isCare(me))
        .mapToDouble(MissionExecution::dayPercentage)
        .sum();
  }

  private boolean isCare(MissionExecution me) {
    var mission = me.mission();
    return mission.isCare(careProductCodeSupplier.get());
  }

  public void saveInvoiceReference(InvoiceForm invoiceForm, Worker worker) {
    var invoiceReference =
        new InvoiceReference(randomUUID().toString(), invoiceForm.yearMonth(), null, worker);
    invoiceReferenceRepository.saveInvoiceReference(invoiceReference);
  }

  public String generateInvoiceFileName(Worker worker) {
    var savedInvoice =
        invoiceReferenceRepository.findInvoiceReferenceByWorker(worker).stream()
            .sorted(comparing(InvoiceReference::autoincrement, naturalOrder()).reversed())
            .toList()
            .getFirst();

    return String.format("FAC-NUM-2025-%s-%s.pdf", worker.code(), savedInvoice.autoincrement());
  }

  public String getInvoiceBucketKey(Worker worker, YearMonth yearMonth) {
    var invoiceReference =
        invoiceReferenceRepository.findInvoiceReferenceByWorker(worker).stream()
            .filter(ref -> ref.yearMonth().equals(yearMonth))
            .sorted(comparing(InvoiceReference::autoincrement, naturalOrder()).reversed())
            .findFirst()
            .get();

    return String.format("FAC-NUM-2025-%s-%s.pdf", worker.code(), invoiceReference.autoincrement());
  }
}
