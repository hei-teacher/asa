package school.hei.asa.repository.model;

import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

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

  @JdbcTypeCode(NAMED_ENUM)
  @Column(nullable = false)
  private JWorkerLevelEnum level;

  @Column(name = "entrance_instant", nullable = false)
  private Instant entranceInstant;
}
