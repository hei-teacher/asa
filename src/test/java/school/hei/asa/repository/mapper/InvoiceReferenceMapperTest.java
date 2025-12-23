package school.hei.asa.repository.mapper;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.repository.model.WorkerType.studentContractor;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.InvoiceReference;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JInvoiceReference;
import school.hei.asa.repository.model.JWorker;

public class InvoiceReferenceMapperTest {
  private final InvoiceReferenceMapper invoiceReferenceMapper =
      new InvoiceReferenceMapper(new WorkerMapper());

  @Test
  void mapping_to_domain() {
    var jInvoiceDetails = newJInvoiceDetails();
    var expected =
        new InvoiceReference(
            "id", YearMonth.parse("2025-01", ofPattern("yyyy-MM")), null, newWorker());

    var actual = invoiceReferenceMapper.toDomain(jInvoiceDetails);

    assertEquals(expected, actual);
  }

  @Test
  void mapping_to_entity() {
    var invoiceDetails =
        new InvoiceReference(
            "id", YearMonth.parse("2025-01", ofPattern("yyyy-MM")), null, newWorker());
    var expected = newJInvoiceDetails();

    var actual = invoiceReferenceMapper.toEntity(invoiceDetails);

    assertEquals(expected, actual);
  }

  private Worker newWorker() {
    return new StudentContractor(
        "code", "name", "email", "fullname", "address", "city", "NIF", "STAT");
  }

  private JInvoiceReference newJInvoiceDetails() {
    var jInvoiceDetails = new JInvoiceReference();
    jInvoiceDetails.setId("id");
    jInvoiceDetails.setWorker(newJWorker());
    jInvoiceDetails.setYearMonth("2025-01");
    return jInvoiceDetails;
  }

  private JWorker newJWorker() {
    JWorker jWorker = new JWorker();
    jWorker.setCode("code");
    jWorker.setName("name");
    jWorker.setEmail("email");
    jWorker.setFullname("fullname");
    jWorker.setAddress("address");
    jWorker.setCity("city");
    jWorker.setNif("NIF");
    jWorker.setStat("STAT");
    jWorker.setWorkerType(studentContractor);
    return jWorker;
  }
}
