package school.hei.asa.repository.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "worker_level")
@Getter
@Setter
public class JWorkerLevel {
  @Id private String level_id;
  private String level;
}
