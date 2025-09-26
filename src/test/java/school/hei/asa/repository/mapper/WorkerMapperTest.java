package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.asa.repository.model.WorkerType.studentContractor;

import org.junit.jupiter.api.Test;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.repository.model.JWorker;

public class WorkerMapperTest {
  private final WorkerMapper workerMapper = new WorkerMapper();

  @Test
  void mapping_entity_to_domain() {
    var jWorker = new JWorker();
    jWorker.setCode("code");
    jWorker.setName("name");
    jWorker.setEmail("email");
    jWorker.setFullname("fullname");
    jWorker.setAddress("address");
    jWorker.setCity("city");
    jWorker.setNif("NIF");
    jWorker.setStat("STAT");
    jWorker.setWorkerType(studentContractor);

    var actual = workerMapper.toDomain(jWorker);

    var expected =
        new StudentContractor(
            "code", "name", "email", "fullname", "address", "city", "NIF", "STAT");

    assertEquals(expected, actual);
  }

  @Test
  void mapping_domain_to_entity() {
    var expected = new JWorker();
    expected.setCode("code");
    expected.setName("name");
    expected.setEmail("email");
    expected.setFullname("fullname");
    expected.setAddress("address");
    expected.setCity("city");
    expected.setNif("NIF");
    expected.setStat("STAT");
    expected.setWorkerType(studentContractor);

    var worker =
        new StudentContractor(
            "code", "name", "email", "fullname", "address", "city", "NIF", "STAT");

    var actual = workerMapper.toEntity(worker);

    assertEquals(expected, actual);
  }
}
