package school.hei.asa.repository.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "worker_level")
@Getter
@Setter
public class JWorkerLevel {
  @Column(name = "level_id", nullable = false)
  @Id
  private String levelId;

  private String level;
}
