package school.hei.asa.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.repository.jrepository.JInvoiceReferenceRepository;
import school.hei.asa.repository.model.JInvoice;
import school.hei.asa.repository.model.JInvoiceReference;

@ExtendWith(MockitoExtension.class)
class InvoiceFormMapperTest {

  @Mock private JInvoiceReferenceRepository jInvoiceReferenceRepository;

  private InvoiceFormMapper invoiceFormMapper;

  @BeforeEach
  void setUp() {
    invoiceFormMapper = new InvoiceFormMapper(jInvoiceReferenceRepository);
  }

  @Test
  void toEntity_mapsAllFieldsCorrectly() {
    var invoiceForm =
        new InvoiceForm(
            "invoice-id-1",
            YearMonth.of(2024, 5),
            LocalDate.of(2024, 5, 1),
            LocalDate.of(2024, 5, 10),
            "Frais de scolarité",
            2.0,
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(200),
            true,
            "Frais supplémentaires",
            1.0,
            BigDecimal.valueOf(50),
            BigDecimal.valueOf(50),
            BigDecimal.valueOf(250),
            "250",
            "RIB-123");

    var jInvoiceReference = new JInvoiceReference();
    jInvoiceReference.setId("invoice-id-1");

    when(jInvoiceReferenceRepository.getReferenceById("invoice-id-1"))
        .thenReturn(jInvoiceReference);

    JInvoice result = invoiceFormMapper.toEntity(invoiceForm);

    assertThat(result.getId()).isEqualTo("invoice-id-1");
    assertThat(result.getYearMonth()).isEqualTo("2024-05");
    assertThat(result.getReferenceDate()).isEqualTo(LocalDate.of(2024, 5, 1));
    assertThat(result.getIssueDate()).isEqualTo(LocalDate.of(2024, 5, 10));
    assertThat(result.getDescription()).isEqualTo("Frais de scolarité");
    assertThat(result.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
    assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    assertThat(result.getHasUpgradedLevel()).isTrue();
    assertThat(result.getExtraDescription()).isEqualTo("Frais supplémentaires");
    assertThat(result.getExtraQuantity()).isEqualTo(1.0);
    assertThat(result.getExtraUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
    assertThat(result.getExtraAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
    assertThat(result.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(250));
    assertThat(result.getParsedAmount()).isEqualTo("250");
    assertThat(result.getRib()).isEqualTo("RIB-123");
    assertThat(result.getInvoiceReference()).isEqualTo(jInvoiceReference);

    verify(jInvoiceReferenceRepository).getReferenceById("invoice-id-1");
  }
}
