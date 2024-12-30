package school.hei.asa.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mission_execution")
@Getter
@Setter
public class JMissionExecution {
  @Id private String id;
  private Date date;

  @ManyToOne
  @JoinColumn(name = "mission_code")
  private JMission mission;

  @ManyToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;

  private double execution_percentage;
}
