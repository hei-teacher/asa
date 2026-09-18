package school.hei.asa.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "credit")
@Getter
@Setter
public class JCredit {
  @Id private String id;

  @Column(name = "credit_code", nullable = false)
  private String creditCode;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "divisor", nullable = false)
  private Double divisor;

  @Column(name = "rate", nullable = false)
  private Double rate;

  @Column(name = "taxable", nullable = false)
  private Boolean taxable;
}
