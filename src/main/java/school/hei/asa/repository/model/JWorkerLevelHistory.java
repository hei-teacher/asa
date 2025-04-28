package school.hei.asa.repository.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "worker_level_history")
@Getter
@Setter
public class JWorkerLevelHistory {
  @Id private String id;

  @Column(insertable = false, updatable = false)
  private String worker_code;

  @ManyToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;

  @Column(nullable = false)
  private String level;

  @Column(name = "entrance_instant", nullable = false)
  private Instant entranceInstant;
}
