package school.hei.asa.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "payslip")
@Getter
@Setter
public class JPaySlip {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;

  @Column(name = "year_month", nullable = false)
  private String yearMonth;

  @Column(name = "gross_amount")
  private BigDecimal grossAmount;

  @Column(name = "net_amount")
  private BigDecimal netAmount;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @Column(name = "paid_leave_amount")
  private Double paidLeaveAmount;

  @Column(name = "taken_paid_leave")
  private Double takenPaidLeave;

  @Column(name = "left_paid_leave")
  private Double leftPaidLeave;

  @Column(name = "leave_base_amount")
  private BigDecimal leaveBaseAmount;

  @Column(name = "leave_amount")
  private BigDecimal leaveAmount;

  @ManyToMany
  @JoinTable(
      name = "payslip_tax",
      joinColumns = @JoinColumn(name = "payslip_id"),
      inverseJoinColumns = @JoinColumn(name = "tax_id"))
  @BatchSize(size = 200)
  private List<JTax> taxes;

  @ManyToMany
  @JoinTable(
      name = "payslip_credit",
      joinColumns = @JoinColumn(name = "payslip_id"),
      inverseJoinColumns = @JoinColumn(name = "credit_id"))
  @BatchSize(size = 200)
  private List<JCredit> credits;
}
