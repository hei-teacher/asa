package school.hei.asa.service;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.time.LocalDate.now;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.TextStyle.FULL;
import static java.util.Locale.FRENCH;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.ContractType;
import school.hei.asa.model.Invoice;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.number.NumberConverter;
import school.hei.asa.number.NumberParser;
import school.hei.asa.repository.BankAccountRepository;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.WorkerLevelHistoryRepository;

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

  private static final int DAYS_TO_BE_WORKED_BY_PARTNER = 18;
  private static final int DAYS_TO_BE_WORKED_BY_STUDENT = 10;

  @SneakyThrows
  public Invoice extractInvoice(Worker worker, ThInvoiceForm invoiceForm) {
    var invoiceData = extractInvoiceData(worker, invoiceForm);
    var file = invoicePDFGenerator.apply(worker, invoiceData, "invoice");

    try (PDDocument document = PDDocument.load(file)) {
      var pdfRenderer = new PDFRenderer(document);
      var image = pdfRenderer.renderImageWithDPI(0, 150);

      var baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      var base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

      return new Invoice(base64Image, invoiceData);
    }
  }

  public String generateInvoiceFileName(String invoiceIssueDate, String workerName) {
    var invoiceDate = parse(invoiceIssueDate, ofPattern("dd/MM/yyyy", FRENCH));
    String month = invoiceDate.getMonth().getDisplayName(FULL, FRENCH);
    String capitalizedMonth = month.substring(0, 1).toUpperCase() + month.substring(1);
    return workerName + " - " + capitalizedMonth + ".pdf";
  }

  private ThInvoiceForm extractInvoiceData(Worker worker, ThInvoiceForm invoiceForm) {
    var formatter = ofPattern("dd/MM/yyyy", FRENCH);
    var isEmpty = invoiceForm.reference() == null || invoiceForm.reference().isBlank();
    var today = now();
    var firstDay = today.withDayOfYear(1);
    var workerLevelHistories = workerLevelHistoryRepository.findAllByWorker(worker);
    var hasLevelHistory = !workerLevelHistories.isEmpty();
    var compensation = hasLevelHistory ? workerLevelHistories.getFirst().compensation() : ZERO;
    var dateReference =
        LocalDate.parse(isEmpty ? firstDay.format(formatter) : invoiceForm.reference(), formatter);
    var issueDate = dateReference.plusDays(3).format(formatter);
    var reference = dateReference.format(formatter);
    var firstCurrentMonthDay = dateReference.withDayOfMonth(1);
    var ym = YearMonth.from(dateReference);
    var lastCurrentMonthDay = ym.atEndOfMonth();
    var totalDaysWorked =
        missionExecutionPercentageSumByWorker(worker, firstCurrentMonthDay, lastCurrentMonthDay);
    var contractType =
        hasLevelHistory
            ? workerLevelHistories.getFirst().contractType()
            : ContractType.STUDENT_CONTRACTOR.getValue();
    var isStudentContractor =
        Objects.equals(contractType, ContractType.STUDENT_CONTRACTOR.getValue());
    var unitValue =
        isStudentContractor ? DAYS_TO_BE_WORKED_BY_STUDENT : DAYS_TO_BE_WORKED_BY_PARTNER;
    var unitPriceValue = compensation.divide(valueOf(unitValue), 2, HALF_UP);
    var unitPrice = numberParser.parseToNumber(unitPriceValue);
    var amount =
        isStudentContractor
            ? numberParser.parseToNumber(compensation)
            : numberParser.parseToNumber(unitPriceValue.multiply(valueOf(totalDaysWorked)));
    var parsedAmount = isEmpty ? "" : numberConverter.convertToWords(amount);
    var description = hasLevelHistory ? workerLevelHistories.getFirst().jobTitle() : "";
    var bankAccount = bankAccountRepository.findByWorkerCode(worker.code());

    return new ThInvoiceForm(
        reference,
        issueDate,
        description,
        String.valueOf(totalDaysWorked),
        unitPrice,
        amount,
        amount,
        false,
        invoiceForm.bonusDescription(),
        invoiceForm.bonusQuantity(),
        invoiceForm.unitPrice(),
        invoiceForm.bonusAmount(),
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
}
