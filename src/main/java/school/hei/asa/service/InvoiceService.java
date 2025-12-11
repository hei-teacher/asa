package school.hei.asa.service;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.FRENCH;
import static java.util.UUID.randomUUID;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.Invoice;
import school.hei.asa.model.InvoiceDetails;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.InvoiceDetailsRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerLevelHistoryRepository;
import school.hei.asa.service.utils.NumberConverter;
import school.hei.asa.service.utils.NumberParser;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceService {
  private final InvoicePDFGenerator invoicePDFGenerator;
  private final NumberConverter numberConverter;
  private final NumberParser numberParser;
  private final WorkerLevelHistoryRepository workerLevelHistoryRepository;
  private final MissionExecutionRepository missionExecutionRepository;
  private final BankAccountRepository bankAccountRepository;
  private final CareProductCodeSupplier careProductCodeSupplier;
  private final InvoiceDetailsRepository invoiceDetailsRepository;

  public Optional<InvoiceDetails> findInvoiceDetails(Worker worker, YearMonth yearMonth) {
    log.info("lets get the invoiceDetails");
    var invoiceDetailsList = invoiceDetailsRepository.findInvoiceDetailsByWorker(worker);
    log.info("here is the invoice result: {}", invoiceDetailsList);
    return invoiceDetailsList.stream()
        .filter(invoiceDetails -> invoiceDetails.yearMonth().equals(yearMonth))
        .findFirst();
  }

  @SneakyThrows
  public Invoice extractInvoice(Worker worker, ThInvoiceForm invoiceForm) {
    var invoiceData = extractInvoiceData(worker, invoiceForm);
    File data = invoicePDFGenerator.apply(worker, invoiceData, "invoice");

    try (PDDocument document = PDDocument.load(data)) {
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

      return new Invoice(base64Image, invoiceData);
    }
  }

  public String generateInvoiceFileName(String yearMonth, String workerCode) {
    return String.format("FAC-NUMERMG-%s-%s.pdf", workerCode, yearMonth);
  }

  private InvoiceForm extractInvoiceData(Worker worker, InvoiceForm invoiceForm) {
    var isEmpty = invoiceForm.yearMonth() == null ;
    var workerLevelHistories = workerLevelHistoryRepository.findAllByWorker(worker);
    var hasLevelHistory = !workerLevelHistories.isEmpty();
    var referenceDate = now();
    var issueDate = referenceDate.plusDays(3);
    var ym =
        isEmpty
            ? YearMonth.from(referenceDate)
            : invoiceForm.yearMonth();
    var firstCurrentMonthDay = ym.atDay(1);
    var lastCurrentMonthDay = ym.atEndOfMonth();
    var hasUpgradedLevel =
        hasLevelHistory
            && LocalDate.ofInstant(workerLevelHistories.getFirst().entranceInstant(), UTC)
                .isBefore(lastCurrentMonthDay)
            && LocalDate.ofInstant(workerLevelHistories.getFirst().entranceInstant(), UTC)
                .isAfter(firstCurrentMonthDay);
    if (hasUpgradedLevel) {
      var firstDescription = workerLevelHistories.get(1).jobTitle();
      var secondDescription = workerLevelHistories.getFirst().jobTitle();
      var firstTotalDaysWorked =
          missionExecutionPercentageSumByWorker(
              worker,
              firstCurrentMonthDay,
              LocalDate.ofInstant(workerLevelHistories.getFirst().entranceInstant(), UTC));
      var secondTotalDaysWorked =
          missionExecutionPercentageSumByWorker(
              worker,
              LocalDate.ofInstant(workerLevelHistories.getFirst().entranceInstant(), UTC),
              lastCurrentMonthDay);
      var firstUnitPrice = workerLevelHistories.get(1).compensation();
      var secondUnitPrice = workerLevelHistories.getFirst().compensation();
      var firstAmount = firstUnitPrice.multiply(valueOf(firstTotalDaysWorked));
      var secondAmount = secondUnitPrice.multiply(valueOf(secondTotalDaysWorked));
      var total = firstAmount.add(secondAmount);
      var parsedTotal = numberConverter.convertToWords(numberParser.parseToNumber(total));
      var bankAccount = bankAccountRepository.findByWorkerCode(worker.code());

      return new InvoiceForm(
          ym,
          referenceDate,
          issueDate,
          firstDescription,
          firstTotalDaysWorked,
          firstUnitPrice,
          firstAmount,
          true,
          secondDescription,
          secondTotalDaysWorked,
          secondUnitPrice,
          secondAmount,
          total,
          parsedTotal,
          bankAccount.toString());
    }
    var totalDaysWorked =
        missionExecutionPercentageSumByWorker(worker, firstCurrentMonthDay, lastCurrentMonthDay);
    var unitPrice = hasLevelHistory ? workerLevelHistories.getFirst().compensation() : ZERO;
    var amount = unitPrice.multiply(valueOf(totalDaysWorked));
    var parsedAmount = numberConverter.convertToWords(numberParser.parseToNumber(amount));
    var description = hasLevelHistory ? workerLevelHistories.getFirst().jobTitle() : "";
    var bankAccount = bankAccountRepository.findByWorkerCode(worker.code());
    log.info("this is your thInvoiceForm: {}", invoiceForm);

      return new InvoiceForm(
              ym,
              referenceDate,
              issueDate,
              description,
              totalDaysWorked,
              unitPrice,
              amount,
              true,
              null,
              null,
              null,
              null,
              amount,
              parsedAmount,
              bankAccount.toString());
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

  public void saveInvoice(String fileName, ThInvoiceForm invoiceForm, Worker worker) {
    var invoiceDetails =
        new InvoiceDetails(
            randomUUID().toString(),
            YearMonth.parse(invoiceForm.yearMonth(), DateTimeFormatter.ofPattern("yyyy-MM")),
            fileName,
            worker);
    invoiceDetailsRepository.saveInvoiceDetails(invoiceDetails);
  }
}
