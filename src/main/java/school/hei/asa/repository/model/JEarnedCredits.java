package school.hei.asa.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "earned_credits")
@Getter
@Setter
public class JEarnedCredits {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "credit_code", referencedColumnName = "credit_code")
  private JCredit credit;

  @Column(name = "number_of_units", nullable = false)
  private BigDecimal numberOfUnits;

  @Column(name = "year_month", nullable = false)
  private String yearMonth;

  @ManyToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;
}
