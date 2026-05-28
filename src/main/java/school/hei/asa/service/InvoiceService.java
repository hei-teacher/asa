package school.hei.asa.service;

import static java.time.LocalDate.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.file.bucket.BucketConf;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.number.NumberConverter;
import school.hei.asa.number.NumberParser;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.InvoiceReferenceRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceService {
  private final NumberConverter numberConverter;
  private final NumberParser numberParser;
  private final ContractRepository contractRepository;
  private final MissionExecutionRepository missionExecutionRepository;
  private final BankAccountRepository bankAccountRepository;
  private final InvoiceReferenceRepository invoiceReferenceRepository;
  private final MissionService missionService;
  private final EventProducer<NewInvoiceGenerated> eventProducer;
  private final BucketComponent bucketComponent;
  private static final String INVOICES_FOLDER = "invoices/";
  private final BucketConf bucketConf;
  private final PDFScrapper pdfScrapper;

  public Optional<InvoiceReference> findInvoiceReference(Worker worker, YearMonth yearMonth) {
    var invoiceReferenceList = invoiceReferenceRepository.findInvoiceReferenceByWorker(worker);
    log.info("here is the invoice result: {}", invoiceReferenceList);
    return invoiceReferenceList.stream()
        .filter(invoiceReference -> invoiceReference.yearMonth().equals(yearMonth))
        .findFirst();
  }

  public InvoiceForm extractInvoiceData(Worker worker, InvoiceForm invoiceForm) {
    var isEmpty = invoiceForm.yearMonth() == null;
    var contracts = contractRepository.findAllByWorker(worker);
    var hasContract = !contracts.isEmpty();
    var referenceDate = now();
    var issueDate = referenceDate.plusDays(3);
    var yearMonth = isEmpty ? YearMonth.from(referenceDate) : invoiceForm.yearMonth();
    var firstCurrentMonthDay = yearMonth.atDay(1);
    var lastCurrentMonthDay = yearMonth.atEndOfMonth();
    var hasUpgradedLevel =
        hasContract
            && LocalDate.ofInstant(contracts.getFirst().entranceInstant(), UTC)
                .isBefore(lastCurrentMonthDay)
            && LocalDate.ofInstant(contracts.getFirst().entranceInstant(), UTC)
                .isAfter(firstCurrentMonthDay);
    var bankAccount = bankAccountRepository.findByWorkerCode(worker.code());
    if (hasUpgradedLevel) {
      var firstContract = contracts.getFirst();
      var secondContract = contracts.get(1);
      var firstTotalDaysWorked =
          missionExecutionPercentageSumByWorker(
              worker,
              firstCurrentMonthDay,
              LocalDate.ofInstant(firstContract.entranceInstant(), UTC));
      var secondTotalDaysWorked =
          missionExecutionPercentageSumByWorker(
              worker,
              LocalDate.ofInstant(firstContract.entranceInstant(), UTC),
              lastCurrentMonthDay);
      var firstInvoiceForm = generateInvoiceFormFrom(firstTotalDaysWorked, firstContract);
      var secondInvoiceForm = generateInvoiceFormFrom(secondTotalDaysWorked, secondContract);
      var total = firstInvoiceForm.amount().add(secondInvoiceForm.amount());
      var parsedTotal = numberConverter.convertToWords(numberParser.parseToNumber(total));
      return new InvoiceForm(
          invoiceForm.id(),
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
    var contract = hasContract ? contracts.getFirst() : null;
    var tempResult = generateInvoiceFormFrom(totalDaysWorked, contract);
    return new InvoiceForm(
        invoiceForm.id(),
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

  private InvoiceForm generateInvoiceFormFrom(Double totalDaysWorked, Contract contract) {
    if (contract == null) {
      return new InvoiceForm(
          null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
          null);
    }
    var contractLevel = contract.level();
    Double unitPrice =
        switch (contractLevel.type()) {
          case partnerContractor, studentContractor -> contractLevel.dailyPay();
          case fullTimeEmployee -> null;
        };
    var amount = BigDecimal.valueOf(totalDaysWorked * unitPrice);
    var parsedAmount = numberConverter.convertToWords(numberParser.parseToNumber(amount));
    var description = contract.jobTitle();

    return new InvoiceForm(
        null,
        null,
        null,
        null,
        description,
        totalDaysWorked,
        BigDecimal.valueOf(unitPrice),
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
        .filter(me -> !missionService.isUnpaidCare(me))
        .mapToDouble(MissionExecution::dayPercentage)
        .sum();
  }

  public void saveInvoiceReference(InvoiceForm invoiceForm, Worker worker) {
    var invoiceReference =
        new InvoiceReference(invoiceForm.id(), invoiceForm.yearMonth(), null, worker);
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

  public InvoiceReference getInvoiceReference(String invoiceId) {
    var invoiceReference = invoiceReferenceRepository.findInvoiceReferenceByInvoiceId(invoiceId);
    return invoiceReference.orElseThrow(
        () -> new NoSuchElementException("Invoice reference not found"));
  }

  @SneakyThrows
  public void sendGenerateInvoiceEvent(String invoiceId) {
    var event = NewInvoiceGenerated.builder().invoiceId(invoiceId).build();
    eventProducer.accept(List.of(event));
  }

  public long getInvoiceTotalAmountByMonth(YearMonth yearMonth) {
    var invoices = downloadInvoiceByYearMonth(yearMonth);
    return invoices.stream().map(pdfScrapper::extractTotalAmount).reduce(0L, Long::sum);
  }

  public List<File> downloadInvoiceByYearMonth(YearMonth yearMonth) {
    ListObjectsV2Request request =
        ListObjectsV2Request.builder()
            .bucket(bucketComponent.getBucketName())
            .prefix(INVOICES_FOLDER)
            .build();

    return bucketConf.getS3Client().listObjectsV2Paginator(request).contents().stream()
        .filter(invoice -> YearMonth.from(invoice.lastModified()).equals(yearMonth))
        .map(item -> bucketComponent.download(item.key()))
        .collect(Collectors.toList());
  }
}
