package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.repository.model.WorkerType.studentContractor;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.repository.model.JWorkerLevel;
import school.hei.asa.repository.model.JWorkerLevelHistory;

public class WorkerLevelHistoryMapperTest {
  private final WorkerLevelHistoryMapper workerLevelHistoryMapper =
      new WorkerLevelHistoryMapper(new WorkerMapper());

  @Test
  void mapping_to_domain() {
    var expected = newModel();
    var jWorkerLevelHistory = newEntity();

    var actual = workerLevelHistoryMapper.toDomain(jWorkerLevelHistory, new Cache());

    assertEquals(expected, actual);
  }

  private JWorkerLevelHistory newEntity() {
    JWorkerLevelHistory jWorkerLevelHistory = new JWorkerLevelHistory();
    jWorkerLevelHistory.setId("id");
    jWorkerLevelHistory.setWorker_code("code");
    jWorkerLevelHistory.setWorker(newJWorker());
    jWorkerLevelHistory.setLevel(newJWorkerLevel());
    jWorkerLevelHistory.setEntranceInstant(newInstant());
    jWorkerLevelHistory.setContractType(String.valueOf(studentContractor));
    jWorkerLevelHistory.setTotalWorkDays(100);
    jWorkerLevelHistory.setSalary(BigDecimal.valueOf(50_000d));
    jWorkerLevelHistory.setJobTitle("job title");
    jWorkerLevelHistory.setContractDuration(24);

    return jWorkerLevelHistory;
  }

  private WorkerLevelHistory newModel() {
    return new WorkerLevelHistory(
        newWorker(),
        newJWorkerLevel(),
        newInstant(),
        String.valueOf(studentContractor),
        100,
        BigDecimal.valueOf(50_000d),
        "job title",
        24);
  }

  private JWorker newJWorker() {
    JWorker jWorker = new JWorker();
    jWorker.setCode("code");
    jWorker.setName("name");
    jWorker.setEmail("email");
    jWorker.setFullname("fullname");
    jWorker.setAddress("address");
    jWorker.setCity("city");
    jWorker.setNif("NIF");
    jWorker.setStat("STAT");
    jWorker.setWorkerType(studentContractor);

    return jWorker;
  }

  private Worker newWorker() {
    return new StudentContractor(
        "code", "name", "email", "fullname", "address", "city", "NIF", "STAT");
  }

  private JWorkerLevel newJWorkerLevel() {
    JWorkerLevel jWorkerLevel = new JWorkerLevel();
    jWorkerLevel.setLevel("level");
    jWorkerLevel.setLevelId("levelId");

    return jWorkerLevel;
  }

  private Instant newInstant() {
    return Instant.ofEpochSecond(1735689600);
  }
}
