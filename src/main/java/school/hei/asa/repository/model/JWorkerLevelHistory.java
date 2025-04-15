package school.hei.asa.repository.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JWorkerLevelEnum level;

  @Column(name = "entrance_instant", nullable = false)
  private Instant entranceInstant;
}
