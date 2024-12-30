package school.hei.asa.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
public class JProduct {
  @Id private String code;
  private String name;
  private String description;

  @OneToMany
  @JoinColumn(name = "product_code")
  private List<JMission> missions;
}