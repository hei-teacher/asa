package school.hei.asa.repository;

import jakarta.transaction.Transactional;
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
    return bankAccountMapper.toDomain(jBankAccountRepository.findByWorkerCode(workerCode));
  }
}
