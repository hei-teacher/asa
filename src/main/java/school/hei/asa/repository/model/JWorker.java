package school.hei.asa.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "worker")
@Getter
@Setter
public class JWorker {
  @Id private String code;
  private String name;
  private String email;
  private String fullname;
  private String address;
  private String city;
  private String nif;
  private String stat;

  @Enumerated(STRING)
  private WorkerType workerType;

  @OneToMany
  @JoinColumn(name = "worker_code")
  @BatchSize(size = 200)
  private List<JMissionExecution> missionExecutions;
}
