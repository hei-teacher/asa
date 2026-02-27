package school.hei.asa.endpoint.rest.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.mapper.ThInvoiceFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.model.InvoiceForm;

public class ThInvoiceFormMapperTest extends FacadeIT {
  @Autowired private ThInvoiceFormMapper thInvoiceFormMapper;

  @Test
  void can_map_to_th() {
    var id = UUID.randomUUID().toString();
    InvoiceForm subject =
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

    ThInvoiceForm expected =
        new ThInvoiceForm(
            id,
            "2024-02",
            "01/02/2026",
            "28/02/2024",
            "Prestation Février",
            "160.0",
            "50.00",
            "8000.00",
            true,
            "Frais de déplacement",
            "1",
            "150",
            "150",
            "8150",
            "Huit mille cent cinquante",
            "FR7612345678901234567890123");

    var actual = thInvoiceFormMapper.toTh(subject);
    Assertions.assertEquals(expected.description(), actual.description());
    Assertions.assertEquals(expected.hasUpgradedLevel(), actual.hasUpgradedLevel());
    Assertions.assertEquals(0, expected.extraAmount().compareTo(actual.extraAmount()));
    Assertions.assertEquals(expected.parsedAmount(), actual.parsedAmount());
    Assertions.assertEquals(expected.issueDate(), actual.issueDate());
  }

  @Test
  void can_map_to_invoice_form() {
    var id = UUID.randomUUID().toString();

    ThInvoiceForm subject =
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

    InvoiceForm expected =
        new InvoiceForm(
            id,
            YearMonth.of(2024, 2),
            LocalDate.of(2024, 2, 1),
            LocalDate.of(2024, 2, 28),
            "Prestation Février",
            160.0,
            new BigDecimal("50.00"),
            new BigDecimal("8000.00"),
            true,
            "Frais de déplacement",
            1.0,
            new BigDecimal("150.00"),
            new BigDecimal("150.00"),
            new BigDecimal("8150.00"),
            "Huit mille cent cinquante",
            "FR7612345678901234567890123");

    var actual = thInvoiceFormMapper.toDomain(subject);
    Assertions.assertEquals(expected.description(), actual.description());
    Assertions.assertEquals(expected.hasUpgradedLevel(), actual.hasUpgradedLevel());
    Assertions.assertEquals(expected.extraAmount(), actual.extraAmount());
    Assertions.assertEquals(expected.amount(), actual.amount());
    Assertions.assertEquals(expected.extraUnitPrice(), actual.extraUnitPrice());
    Assertions.assertEquals(expected.parsedAmount(), actual.parsedAmount());
  }
}
