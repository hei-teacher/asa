package school.hei.asa.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Table(name = "invoice_data")
@Getter
public class JInvoice {
  @Id private String id;
  private String yearMonth;
  private LocalDate referenceDate;
  private LocalDate issueDate;
  private String description;
  private BigDecimal unitPrice;
  private BigDecimal amount;
  private Boolean hasUpgradedLevel;
  private String extraDescription;
  private Double extraQuantity;
  private BigDecimal extraUnitPrice;
  private BigDecimal extraAmount;
  private BigDecimal total;
  private String parsedAmount;
  private String rib;

  @OneToOne
  @JoinColumn(name = "invoice_ref_id", referencedColumnName = "id")
  private JInvoiceReference invoiceReference;
}
