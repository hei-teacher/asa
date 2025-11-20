package school.hei.asa.repository.mapper;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.repository.model.WorkerType.studentContractor;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.InvoiceDetails;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JInvoiceDetails;
import school.hei.asa.repository.model.JWorker;

public class InvoiceDetailsMapperTest {
  private final InvoiceDetailsMapper invoiceDetailsMapper =
      new InvoiceDetailsMapper(new WorkerMapper());

  @Test
  void mapping_to_domain() {
    var jInvoiceDetails = newJInvoiceDetails();
    var expected =
        new InvoiceDetails(
            "id", YearMonth.parse("2025-01", ofPattern("yyyy-MM")), "ref", newWorker());

    var actual = invoiceDetailsMapper.toDomain(jInvoiceDetails);

    assertEquals(expected, actual);
  }

  @Test
  void mapping_to_entity() {
    var invoiceDetails =
        new InvoiceDetails(
            "id", YearMonth.parse("2025-01", ofPattern("yyyy-MM")), "ref", newWorker());
    var expected = newJInvoiceDetails();

    var actual = invoiceDetailsMapper.toEntity(invoiceDetails);

    assertEquals(expected, actual);
  }

  private Worker newWorker() {
    return new StudentContractor(
        "code", "name", "email", "fullname", "address", "city", "NIF", "STAT");
  }

  private JInvoiceDetails newJInvoiceDetails() {
    var jInvoiceDetails = new JInvoiceDetails();
    jInvoiceDetails.setReference("ref");
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
