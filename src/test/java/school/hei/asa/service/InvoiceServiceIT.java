package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.PartnerContractor;

public class InvoiceServiceIT extends FacadeIT {
  @Autowired InvoiceService invoiceService;

  @Test
  void can_generate_invoice_bucketKey() {
    var invoiceData =
        new InvoiceForm(
            YearMonth.of(2025, Month.JANUARY),
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
    var worker = new PartnerContractor("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");
    invoiceService.saveInvoiceReference(invoiceData, worker);

    var expected = invoiceService.generateInvoiceFileName(worker);

    var actual = invoiceService.getInvoiceBucketKey(worker, YearMonth.of(2025, Month.JANUARY));

    assertEquals(expected, actual);
  }
}
