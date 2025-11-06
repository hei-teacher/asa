package school.hei.asa.repository.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.BankAccount;
import school.hei.asa.repository.model.JBankAccount;

@Component
@AllArgsConstructor
public class BankAccountMapper {
  private final WorkerMapper workerMapper;

  public BankAccount toDomain(JBankAccount jBankAccount) {
    return new BankAccount(
        jBankAccount.getBank(),
        jBankAccount.getAgency(),
        jBankAccount.getAccount(),
        jBankAccount.getKey(),
        jBankAccount.getIban(),
        workerMapper.toDomain(jBankAccount.getWorker()));
  }
}
