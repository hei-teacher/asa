package school.hei.asa.repository.jrepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JBankAccount;

@Repository
public interface JBankAccountRepository extends JpaRepository<JBankAccount, String> {
  @Override
  List<JBankAccount> findAll();

  JBankAccount findByWorkerCode(String workerCode);
}
