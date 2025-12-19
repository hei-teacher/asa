package school.hei.asa.repository;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.PartnerContractor;

public class InvoiceReferenceRepositoryTest extends FacadeIT {
  @Autowired InvoiceReferenceRepository invoiceReferenceRepository;

  @Test
  void fetch_all_invoice_details_for_worker() {
    var worker = new PartnerContractor("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");
    var invoiceDetails1 =
        new InvoiceReference("id1", YearMonth.parse("2025-01", ofPattern("yyyy-MM")), 1, worker);
    var invoiceDetails2 =
        new InvoiceReference("id2", YearMonth.parse("2025-02", ofPattern("yyyy-MM")), 2, worker);
    var invoiceDetails3 =
        new InvoiceReference("id3", YearMonth.parse("2025-03", ofPattern("yyyy-MM")), 3, worker);
    var invoiceDetails4 =
        new InvoiceReference("id4", YearMonth.parse("2025-04", ofPattern("yyyy-MM")), 4, worker);
    var expected = List.of(invoiceDetails1, invoiceDetails2, invoiceDetails3, invoiceDetails4);

    var actual = invoiceReferenceRepository.findInvoiceReferenceByWorker(worker);

    assertEquals(expected, actual);
  }

  @Test
  void save_invoice_details_for_worker() {
    var worker = new PartnerContractor("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");
    var expected =
        new InvoiceReference("id5", YearMonth.parse("2025-05", ofPattern("yyyy-MM")), 5, worker);

    invoiceReferenceRepository.saveInvoiceReference(expected);

    var actual = invoiceReferenceRepository.findInvoiceReferenceByWorker(worker);

    assertTrue(actual.contains(expected));
  }
}
