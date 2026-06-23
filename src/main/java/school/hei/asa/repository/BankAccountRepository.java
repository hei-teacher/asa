package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.BankAccount;
import school.hei.asa.repository.jrepository.JBankAccountRepository;
import school.hei.asa.repository.mapper.BankAccountMapper;

@AllArgsConstructor
@Component
public class BankAccountRepository {
  private final JBankAccountRepository jBankAccountRepository;
  private final BankAccountMapper bankAccountMapper;

  @Transactional
  public BankAccount findByWorkerCode(String workerCode) {
    return jBankAccountRepository
        .findByWorkerCode(workerCode)
        .map(bankAccountMapper::toDomain)
        .orElse(null);
  }

  @Transactional
  public List<BankAccount> findAll() {
    return jBankAccountRepository.findAll().stream().map(bankAccountMapper::toDomain).toList();
  }
}
