package school.hei.asa.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.rest.controller.mapper.ThWorkerMapper;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;

class ThWorkerMapperTest {

  private final ThWorkerMapper mapper = new ThWorkerMapper();

  @Test
  void toWorkerType_returns_Prestataire_for_partnerContractor() {
    assertEquals("Prestataire", mapper.toWorkerType("partnerContractor"));
  }

  @Test
  void toWorkerType_returns_Salarie_for_fullTimeEmployee() {
    assertEquals("Salarié", mapper.toWorkerType("fullTimeEmployee"));
  }

  @Test
  void toWorkerType_returns_empty_for_null() {
    assertEquals("", mapper.toWorkerType(null));
  }

  @Test
  void toWorkerType_returns_Alternant_for_other() {
    assertEquals("Alternant", mapper.toWorkerType("studentContractor"));
  }

  @Test
  void toThWorker_with_contracts_maps_correctly() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var now = Instant.now();
    var level = new ContractLevel("L1", ContractType.partnerContractor, null, 200.0);
    var contract =
        new Contract(worker, "Dev", level, now, null, Duration.ofDays(365), "Company", "key");

    var result = mapper.toThWorker(worker, List.of(contract));

    assertEquals("W-001", result.code());
    assertEquals("John", result.name());
    assertEquals("Prestataire", result.workerType());
    assertEquals(now, result.entranceInstant());
    assertEquals("L1", result.level());
    assertEquals(now, result.levelEntranceInstant());
  }

  @Test
  void toThWorker_without_contracts_returns_null_fields() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

    var result = mapper.toThWorker(worker, List.of());

    assertEquals("W-001", result.code());
    assertNull(result.entranceInstant());
    assertNull(result.level());
    assertNull(result.levelEntranceInstant());
  }
}
