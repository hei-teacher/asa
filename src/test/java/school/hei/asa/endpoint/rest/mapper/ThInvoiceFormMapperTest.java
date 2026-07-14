package school.hei.asa.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.rest.controller.mapper.ThInvoiceFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.number.NumberParser;

class ThInvoiceFormMapperTest {
  private final ThInvoiceFormMapper mapper = new ThInvoiceFormMapper(new NumberParser());

  @Test
  void can_map_to_th() {
    var id = UUID.randomUUID().toString();
    var subject =
        new InvoiceForm(
            id,
            YearMonth.of(2024, 2),
            LocalDate.of(2024, 2, 1),
            LocalDate.of(2024, 2, 28),
            "Prestation Février",
            160.0,
            new BigDecimal("50"),
            new BigDecimal("8000"),
            true,
            "Frais de déplacement",
            1.0,
            new BigDecimal("150"),
            new BigDecimal("150"),
            new BigDecimal("8150"),
            "Huit mille cent cinquante",
            "FR7612345678901234567890123");

    var actual = mapper.toTh(subject);

    assertEquals(subject.description(), actual.description());
    assertEquals(subject.hasUpgradedLevel(), actual.hasUpgradedLevel());
    assertEquals(subject.parsedAmount(), actual.parsedAmount());
  }

  @Test
  void can_map_to_invoice_form() {
    var id = UUID.randomUUID().toString();
    var subject =
        new ThInvoiceForm(
            id,
            "2024-02",
            "01/02/2024",
            "28/02/2024",
            "Prestation Février",
            "160.0",
            "50.00",
            "8000.00",
            true,
            "Frais de déplacement",
            "1.0",
            "150.00",
            "150.00",
            "8150.00",
            "Huit mille cent cinquante",
            "FR7612345678901234567890123");

    var actual = mapper.toDomain(subject);

    assertEquals(subject.description(), actual.description());
    assertEquals(subject.hasUpgradedLevel(), actual.hasUpgradedLevel());
    assertEquals(subject.parsedAmount(), actual.parsedAmount());
  }

  @Test
  void toDomain_with_null_fields_returns_partial() {
    var subject =
        new ThInvoiceForm(
            "id", null, null, null, "desc", null, null, null, false, null, null, null, null, null,
            null, null);

    var actual = mapper.toDomain(subject);

    assertNull(actual.yearMonth());
    assertNull(actual.referenceDate());
    assertNull(actual.issueDate());
    assertEquals("desc", actual.description());
  }

  @Test
  void toDomain_with_null_extra_fields_returns_null_extras() {
    var subject =
        new ThInvoiceForm(
            "id",
            "2024-02",
            "01/02/2024",
            "28/02/2024",
            "desc",
            "160.0",
            "50.00",
            "8000.00",
            false,
            null,
            "null",
            null,
            null,
            "8150.00",
            null,
            null);

    var actual = mapper.toDomain(subject);

    assertNull(actual.extraQuantity());
    assertNull(actual.extraUnitPrice());
    assertNull(actual.extraAmount());
  }
}
