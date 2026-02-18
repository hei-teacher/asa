package school.hei.asa.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contract")
@Getter
@Setter
public class JContract {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;

  @ManyToOne
  @JoinColumn(name = "level")
  private JContractLevel level;

  @Column(name = "entrance_instant", nullable = false)
  private Instant entranceInstant;

  @Column(name = "end_instant")
  private Instant endInstant;

  @Column(name = "job_title")
  private String jobTitle;

  @Column(name = "duration_in_days")
  private Integer durationInDays;

  @Column(name = "contract_bucket_key")
  private String contractBucketKey;
}
