package school.hei.asa.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.controller.mapper.ThWorkerMapper;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.model.contract.ContractType;
import school.hei.asa.service.ContractService;

class ThContractMapperTest {

  @Test
  void toTh_maps_contracts() {
    var thWorkerMapper = mock(ThWorkerMapper.class);
    var contractService = mock(ContractService.class);
    var mapper = new ThContractMapper(thWorkerMapper, contractService);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    Instant now = Instant.now();
    var contractLevel = new ContractLevel("L1", ContractType.fullTimeEmployee, 5000.0, null);
    var contract =
        new Contract(
            worker, "Dev", contractLevel, now, null, Duration.ofDays(365), "Company", "key");
    when(thWorkerMapper.toWorkerType("fullTimeEmployee")).thenReturn("Salarié");
    when(contractService.getActualWorkedDaysByDateByWorker(any(), any(), any())).thenReturn("20");

    var result = mapper.toTh(List.of(contract));

    assertEquals(1, result.size());
    var thContract = result.get(0);
    assertEquals("L1", thContract.level());
    assertEquals("Salarié", thContract.contractType());
    assertEquals(BigDecimal.valueOf(5000.0), thContract.compensation());
  }

  @Test
  void toThContractsByWorker_maps_correctly() {
    var thWorkerMapper = mock(ThWorkerMapper.class);
    var contractService = mock(ContractService.class);
    var mapper = new ThContractMapper(thWorkerMapper, contractService);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    Instant now = Instant.now();
    var contractLevel = new ContractLevel("L1", ContractType.fullTimeEmployee, 5000.0, null);
    var contract =
        new Contract(
            worker, "Dev", contractLevel, now, null, Duration.ofDays(365), "Company", "key");
    when(thWorkerMapper.toWorkerType("fullTimeEmployee")).thenReturn("Salarié");
    when(contractService.getActualWorkedDaysByDateByWorker(any(), any(), any())).thenReturn("20");

    var result = mapper.toThContractsByWorker(Map.of(worker, List.of(contract)));

    assertEquals(1, result.size());
    assertTrue(result.containsKey(worker));
  }
}
