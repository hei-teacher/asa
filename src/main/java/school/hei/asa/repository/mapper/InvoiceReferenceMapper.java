package school.hei.asa.repository.mapper;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.repository.model.JInvoiceReference;

@Slf4j
@Component
@AllArgsConstructor
public class InvoiceReferenceMapper {
  private final WorkerMapper workerMapper;

  public InvoiceReference toDomain(JInvoiceReference jInvoiceReference) {
    log.info("jinvoiceRef = {}", jInvoiceReference);
    return new InvoiceReference(
        jInvoiceReference.getId(),
        YearMonth.parse(jInvoiceReference.getYearMonth(), DateTimeFormatter.ofPattern("yyyy-MM")),
        jInvoiceReference.getAutoincrement(),
        workerMapper.toDomain(jInvoiceReference.getWorker()));
  }

  public JInvoiceReference toEntity(InvoiceReference invoiceReference) {
    var jInvoiceDetails = new JInvoiceReference();
    jInvoiceDetails.setId(invoiceReference.id());
    jInvoiceDetails.setWorker(workerMapper.toEntity(invoiceReference.worker()));
    jInvoiceDetails.setYearMonth(invoiceReference.yearMonth().toString());
    jInvoiceDetails.setAutoincrement(invoiceReference.autoincrement());
    return jInvoiceDetails;
  }
}
