package school.hei.asa.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "app_settings")
@Getter
@Setter
public class JAppSettings {
  @Id private String id;

  @Column(name = "low_contract_days_threshold")
  private int lowContractDaysThreshold;
}
