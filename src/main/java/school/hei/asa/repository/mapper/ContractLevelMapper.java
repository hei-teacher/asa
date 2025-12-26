package school.hei.asa.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.model.JContract;

@Component
public class ContractLevelMapper {

  public ContractLevel toDomain(JContract jContract) {
    var jLevel = jContract.getLevel();
    var jType = jLevel.getType();
    return new ContractLevel(
        jContract.getLevel().getCode(), jType, jLevel.getMonthlyPay(), jLevel.getDailyPay());
  }
}
