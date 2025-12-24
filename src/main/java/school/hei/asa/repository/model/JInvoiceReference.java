package school.hei.asa.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "invoice")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@ToString
public class JInvoiceReference {
  @Id private String id;

  @Column(name = "year_month")
  private String yearMonth;

  @Generated(GenerationTime.INSERT)
  @Column(name = "autoincrement", updatable = false, insertable = false)
  private Integer autoincrement;

  @ManyToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;
}
