package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import school.hei.asa.model.BankAccount;
import school.hei.asa.repository.model.JBankAccount;
import school.hei.asa.repository.model.JWorker;

class BankAccountMapperTest {

  private final BankAccountMapper bankAccountMapper = new BankAccountMapper(new WorkerMapper());

  @Test
  void mapping_to_domain() {
    var jBankAccount = new JBankAccount();
    jBankAccount.setBank("BNI");
    jBankAccount.setAgency("Antananarivo");
    jBankAccount.setAccount("123456789");
    jBankAccount.setKey("01");
    jBankAccount.setIban("MG123456789");
    jBankAccount.setWorker(newJWorker());

    var actual = bankAccountMapper.toDomain(jBankAccount);

    var expected =
        new BankAccount(
            "BNI",
            "Antananarivo",
            "123456789",
            "01",
            "MG123456789",
            new WorkerMapper().toDomain(newJWorker()));

    assertEquals(expected, actual);
  }

  private JWorker newJWorker() {
    var jWorker = new JWorker();
    jWorker.setCode("W-001");
    jWorker.setName("John Doe");
    jWorker.setEmail("john@test.com");
    jWorker.setFullname("John Doe");
    jWorker.setAddress("Address");
    jWorker.setCity("City");
    jWorker.setNif("NIF001");
    jWorker.setStat("STAT001");
    return jWorker;
  }
}
