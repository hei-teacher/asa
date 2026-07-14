package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import org.junit.jupiter.api.Test;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JContractLevel;

class ContractLevelMapperTest {

  private final ContractLevelMapper contractLevelMapper = new ContractLevelMapper();

  @Test
  void mapping_to_domain() {
    var jContract = new JContract();
    var jLevel = new JContractLevel();
    jLevel.setCode("STD");
    jLevel.setType(studentContractor);
    jLevel.setMonthlyPay(500_000.0);
    jLevel.setDailyPay(25_000.0);
    jContract.setLevel(jLevel);

    var actual = contractLevelMapper.toDomain(jContract);

    var expected = new ContractLevel("STD", studentContractor, 500_000.0, 25_000.0);
    assertEquals(expected, actual);
  }
}
