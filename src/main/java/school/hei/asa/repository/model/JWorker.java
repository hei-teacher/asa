package school.hei.asa.repository.model;

import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "worker")
@Getter
@Setter
public class JWorker {
  @Id private String code;
  private String name;

  @Enumerated(STRING)
  private WorkerType workerType;

  public enum WorkerType {
    partnerContractor,
    studentContractor,
    fullTimeEmployee
  }
}
