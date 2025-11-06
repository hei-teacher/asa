package school.hei.asa.repository.jrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JBankAccount;

import java.util.List;

@Repository
public interface JBankAccountRepository extends JpaRepository<JBankAccount, String> {
    @Override
    List<JBankAccount> findAll();

    JBankAccount findByWorkerCode(String workerCode);
}
