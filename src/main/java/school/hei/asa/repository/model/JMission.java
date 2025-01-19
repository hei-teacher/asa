package school.hei.asa.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "mission")
@Getter
@Setter
public class JMission {
  @Id private String code;
  private String title;
  private String description;
  private int maxDurationInDays;

  @OneToMany
  @JoinColumn(name = "mission_code")
  @BatchSize(size = 200)
  private List<JMissionExecution> missionExecutions;

  @ManyToOne private JProduct product;
}
