package school.hei.asa.service;

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

  private ThInvoiceForm extractInvoiceData(Worker worker, ThInvoiceForm invoiceForm) {
    var formatter = ofPattern("dd/MM/yyyy", FRENCH);
    var isEmpty = invoiceForm.yearMonth() == null || invoiceForm.yearMonth().isBlank();
    var workerLevelHistories = workerLevelHistoryRepository.findAllByWorker(worker);
    var hasLevelHistory = !workerLevelHistories.isEmpty();
    var dateReference = now();
    var issueDate = dateReference.plusDays(3).format(formatter);
    var reference = dateReference.format(formatter);
    var ym =
        isEmpty
            ? YearMonth.from(dateReference)
            : YearMonth.parse(invoiceForm.yearMonth(), ofPattern("yyyy-MM"));
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
      var firstPrice = firstUnitPrice.multiply(valueOf(firstTotalDaysWorked));
      var secondPrice = secondUnitPrice.multiply(valueOf(secondTotalDaysWorked));
      var firstAmount = numberParser.parseToNumber(firstPrice);
      var secondAmount = numberParser.parseToNumber(secondPrice);
      var total = numberParser.parseToNumber(firstPrice.add(secondPrice));
      var parsedTotal = numberConverter.convertToWords(total);
      var bankAccount = bankAccountRepository.findByWorkerCode(worker.code());

      return new ThInvoiceForm(
          ym.format(ofPattern("yyyy-MM")),
          reference,
          issueDate,
          firstDescription,
          String.valueOf(firstTotalDaysWorked),
          String.valueOf(firstUnitPrice),
          firstAmount,
          hasUpgradedLevel,
          secondDescription,
          String.valueOf(secondTotalDaysWorked),
          String.valueOf(secondUnitPrice),
          secondAmount,
          total,
          parsedTotal,
          bankAccount.toString());
    }
    var totalDaysWorked =
        missionExecutionPercentageSumByWorker(worker, firstCurrentMonthDay, lastCurrentMonthDay);
    var unitPrice = workerLevelHistories.getFirst().compensation();
    var amount = numberParser.parseToNumber(unitPrice.multiply(valueOf(totalDaysWorked)));
    var parsedAmount = numberConverter.convertToWords(amount);
    var description = workerLevelHistories.getFirst().jobTitle();
    var bankAccount = bankAccountRepository.findByWorkerCode(worker.code());
    log.info("this is your thInvoiceForm: {}", invoiceForm);

    return new ThInvoiceForm(
        ym.format(ofPattern("yyyy-MM")),
        reference,
        issueDate,
        description,
        String.valueOf(totalDaysWorked),
        String.valueOf(unitPrice),
        amount,
        hasUpgradedLevel,
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
