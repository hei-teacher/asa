package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JContractLevel;
import school.hei.asa.repository.model.JWorker;

public class ContractMapperTest {

  private DailyExecutionRepository dailyExecutionRepository;
  private ContractMapper contractMapper;

  @BeforeEach
  void setUp() {
    dailyExecutionRepository = mock(DailyExecutionRepository.class);
    contractMapper =
        new ContractMapper(new WorkerMapper(), new ContractLevelMapper(), dailyExecutionRepository);
  }

  @Test
  void mapping_to_domain() {
    var w = newWorker();
    var de = mock(DailyExecution.class);
    when(dailyExecutionRepository.findByWorkerCodeAndDateBetween(eq(w.code()), any(), any()))
        .thenReturn(List.of(de));
    var expected = newModel(w, de);
    var jContract = newEntity();

    var actual = contractMapper.toDomain(jContract, new Cache());

    assertEquals(expected, actual);
  }

  private JContract newEntity() {
    JContract jContract = new JContract();
    jContract.setId("id");
    jContract.setWorker(newJWorker());
    jContract.setLevel(newJWorkerLevel());
    jContract.setEntranceInstant(newInstant());

    var jLevel = new JContractLevel();
    jLevel.setType(studentContractor);
    jLevel.setDailyPay(50_000d);
    jLevel.setCode("level");
    jContract.setLevel(jLevel);

    jContract.setJobTitle("job title");
    jContract.setDurationInDays(100);
    jContract.setContractBucketKey("DUMMY_BUCKET");

    return jContract;
  }

  private Contract newModel(Worker w, DailyExecution de) {
    var level = new ContractLevel("level", studentContractor, null, 50_000.);
    return new Contract(
        w,
        "job title",
        level,
        newInstant(),
        null,
        Duration.ofDays(100),
        null,
        List.of(de),
        "DUMMY_BUCKET");
  }

  private JWorker newJWorker() {
    JWorker jWorker = new JWorker();
    jWorker.setCode("code");
    jWorker.setName("code");
    jWorker.setEmail("email");
    jWorker.setFullname("fullname");
    jWorker.setAddress("address");
    jWorker.setCity("city");
    jWorker.setNif("NIF");
    jWorker.setStat("STAT");

    return jWorker;
  }

  private Worker newWorker() {
    return new Worker("code", "code", "email", "fullname", "address", "city", "NIF", "STAT");
  }

  private JContractLevel newJWorkerLevel() {
    JContractLevel jContractLevel = new JContractLevel();
    jContractLevel.setCode("level");

    return jContractLevel;
  }

  private Instant newInstant() {
    return Instant.ofEpochSecond(1735689600);
  }
}
