package school.hei.asa.repository.model;

import jakarta.annotation.Nullable;
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

  @ManyToOne
  @JoinColumn(name = "level")
  private JWorkerLevel level;

  @Column(name = "entrance_instant", nullable = false)
  private Instant entranceInstant;

  @Column(name = "contract_type", nullable = false)
  private String contractType;

  @Nullable
  @Column(name = "total_work_days")
  private Integer totalWorkDays;
}
