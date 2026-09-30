package school.hei.asa.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import school.hei.asa.model.TaxSide;

@Entity
@Table(name = "tax_progression")
@Getter
@Setter
public class JTaxProgression {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "tax_id")
  private JTax tax;

  @Column(name = "min_amount", nullable = false)
  private BigDecimal minAmount;

  @Column(name = "max_amount", nullable = false)
  private BigDecimal maxAmount;

  private Double rate;

  @Enumerated(STRING)
  @Column(name = "tax_side", nullable = false)
  private TaxSide taxSide;

  @Column(name = "default_value", nullable = false)
  private BigDecimal defaultValue;
}
