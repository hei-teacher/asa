package school.hei.asa.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import school.hei.asa.model.DeductionType;
import school.hei.asa.model.TaxType;

@Entity
@Table(name = "deduction")
@Getter
@Setter
public class JTax {
  @Id private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(STRING)
  @Column(name = "tax_type", nullable = false)
  private TaxType taxType;

  @Enumerated(STRING)
  @Column(name = "type", nullable = false)
  private DeductionType deductionType;

  @OneToMany
  @JoinColumn(name = "tax_id")
  @BatchSize(size = 200)
  private List<JTaxProgression> taxProgressions;
}
