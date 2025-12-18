package school.hei.asa.repository.mapper;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.repository.model.JInvoiceReference;

@Component
@AllArgsConstructor
public class InvoiceDetailsMapper {
  private final WorkerMapper workerMapper;

  public InvoiceReference toDomain(JInvoiceReference jInvoiceReference) {
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
