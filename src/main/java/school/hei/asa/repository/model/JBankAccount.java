package school.hei.asa.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rib_mg")
@Getter
@Setter
public class JBankAccount {
  @Id private String id;

  @Column(insertable = false, updatable = false, name = "banque")
  private String bank;

  @Column(insertable = false, updatable = false, name = "agence")
  private String agency;

  @Column(insertable = false, updatable = false, name = "compte")
  private String account;

  @Column(insertable = false, updatable = false, name = "cle")
  private String key;

  @Column(insertable = false, updatable = false, name = "IBAN")
  private String iban;

  @OneToOne
  @JoinColumn(name = "worker_code")
  private JWorker worker;
}
