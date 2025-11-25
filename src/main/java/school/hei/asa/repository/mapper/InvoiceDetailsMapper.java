package school.hei.asa.repository.mapper;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.InvoiceDetails;
import school.hei.asa.repository.model.JInvoiceDetails;

@Component
@AllArgsConstructor
public class InvoiceDetailsMapper {
  private final WorkerMapper workerMapper;

  public InvoiceDetails toDomain(JInvoiceDetails jInvoiceDetails) {
    return new InvoiceDetails(
        jInvoiceDetails.getId(),
        YearMonth.parse(jInvoiceDetails.getYearMonth(), DateTimeFormatter.ofPattern("yyyy-MM")),
        jInvoiceDetails.getReference(),
        workerMapper.toDomain(jInvoiceDetails.getWorker()));
  }

  public JInvoiceDetails toEntity(InvoiceDetails invoiceDetails) {
    var jInvoiceDetails = new JInvoiceDetails();
    jInvoiceDetails.setId(invoiceDetails.id());
    jInvoiceDetails.setWorker(workerMapper.toEntity(invoiceDetails.worker()));
    jInvoiceDetails.setYearMonth(invoiceDetails.yearMonth().toString());
    jInvoiceDetails.setReference(invoiceDetails.reference());
    return jInvoiceDetails;
  }
}
