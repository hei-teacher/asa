package school.hei.asa.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import school.hei.asa.model.contract.ContractType;

@Entity
@Table(name = "contract_level")
@Getter
@Setter
public class JContractLevel {
  @Column(name = "level_id", nullable = false)
  @Id
  private String levelId;

  private String code;

  @Enumerated(STRING)
  private ContractType type;

  @Column(name = "monthly_pay")
  private Double monthlyPay;

  @Column(name = "daily_pay")
  private Double dailyPay;
}
