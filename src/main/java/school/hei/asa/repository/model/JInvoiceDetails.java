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

@Entity
@Table(name = "invoice")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class JInvoiceDetails {
  @Id private String id;

  @Column(name = "year_month")
  private String yearMonth;

  @Column(name = "invoice_reference")
  private String reference;

  @ManyToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;
}
