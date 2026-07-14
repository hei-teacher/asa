package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.Worker;
import school.hei.asa.service.ContractService;

class ThContractServiceTest {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

  @Test
  void generateCSV_without_workerCode_generates_all_csv() {
    var contractService = mock(ContractService.class);
    var thContractMapper = mock(ThContractMapper.class);
    var service = new ThContractService(contractService, thContractMapper);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var entranceDate = formatter.format(LocalDate.now());
    var thContract =
        new ThContract(
            "L1",
            entranceDate,
            "-",
            "Salarié",
            BigDecimal.valueOf(2000),
            "Company",
            "Dev",
            "30",
            "key",
            "20");
    when(contractService.totalWorkDaysPerWorker()).thenReturn(Map.of(worker, List.of()));
    when(thContractMapper.toThContractsByWorker(any()))
        .thenReturn(Map.of(worker, List.of(thContract)));

    var result = service.generateCSV(null);

    assertNotNull(result);
    assertTrue(result.exists());
    assertTrue(result.getName().contains("All"));
    result.delete();
  }

  @Test
  void generateCSV_with_workerCode_generates_specific_csv() {
    var contractService = mock(ContractService.class);
    var thContractMapper = mock(ThContractMapper.class);
    var service = new ThContractService(contractService, thContractMapper);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var entranceDate = formatter.format(LocalDate.now());
    var thContract =
        new ThContract(
            "L1",
            entranceDate,
            "-",
            "Salarié",
            BigDecimal.valueOf(2000),
            "Company",
            "Dev",
            "30",
            "key",
            "20");
    when(contractService.totalWorkDaysForOneWorker("W-001")).thenReturn(Map.of(worker, List.of()));
    when(thContractMapper.toThContractsByWorker(any()))
        .thenReturn(Map.of(worker, List.of(thContract)));

    var result = service.generateCSV("W-001");

    assertNotNull(result);
    assertTrue(result.exists());
    assertTrue(result.getName().contains("John"));
    result.delete();
  }

  @Test
  void totalWorkDaysPerWorker_returns_mapped_result() {
    var contractService = mock(ContractService.class);
    var thContractMapper = mock(ThContractMapper.class);
    var service = new ThContractService(contractService, thContractMapper);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var entranceDate = formatter.format(LocalDate.now());
    var thContract =
        new ThContract(
            "L1",
            entranceDate,
            "-",
            "Salarié",
            BigDecimal.valueOf(2000),
            "Company",
            "Dev",
            "30",
            "key",
            "20");
    var expected = Map.of(worker, List.of(thContract));

    when(contractService.totalWorkDaysPerWorker()).thenReturn(Map.of(worker, List.of()));
    when(thContractMapper.toThContractsByWorker(any())).thenReturn(expected);

    var result = service.totalWorkDaysPerWorker();

    assertEquals(expected, result);
    verify(contractService).totalWorkDaysPerWorker();
    verify(thContractMapper).toThContractsByWorker(any());
  }

  @Test
  void totalWorkDaysForOneWorker_returns_mapped_result() {
    var contractService = mock(ContractService.class);
    var thContractMapper = mock(ThContractMapper.class);
    var service = new ThContractService(contractService, thContractMapper);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var entranceDate = formatter.format(LocalDate.now());
    var thContract =
        new ThContract(
            "L1",
            entranceDate,
            "-",
            "Salarié",
            BigDecimal.valueOf(2000),
            "Company",
            "Dev",
            "30",
            "key",
            "20");
    var expected = Map.of(worker, List.of(thContract));

    when(contractService.totalWorkDaysForOneWorker("W-001")).thenReturn(Map.of(worker, List.of()));
    when(thContractMapper.toThContractsByWorker(any())).thenReturn(expected);

    var result = service.totalWorkDaysForOneWorker("W-001");

    assertEquals(expected, result);
    verify(contractService).totalWorkDaysForOneWorker("W-001");
    verify(thContractMapper).toThContractsByWorker(any());
  }

  @Test
  void generateCSV_with_blank_workerCode_calls_totalWorkDaysPerWorker() {
    var contractService = mock(ContractService.class);
    var thContractMapper = mock(ThContractMapper.class);
    var service = new ThContractService(contractService, thContractMapper);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var entranceDate = formatter.format(LocalDate.now());
    var thContract =
        new ThContract(
            "L1",
            entranceDate,
            "-",
            "Salarié",
            BigDecimal.valueOf(2000),
            "Company",
            "Dev",
            "30",
            "key",
            "20");
    when(contractService.totalWorkDaysPerWorker()).thenReturn(Map.of(worker, List.of()));
    when(thContractMapper.toThContractsByWorker(any()))
        .thenReturn(Map.of(worker, List.of(thContract)));

    var result = service.generateCSV("");

    assertNotNull(result);
    assertTrue(result.exists());
    assertTrue(result.getName().contains("All"));
    verify(contractService).totalWorkDaysPerWorker();
    verify(contractService, never()).totalWorkDaysForOneWorker(any());
    result.delete();
  }

  @Test
  void generateCSV_handles_dash_actualWorkedDays() throws java.io.IOException {
    var contractService = mock(ContractService.class);
    var thContractMapper = mock(ThContractMapper.class);
    var service = new ThContractService(contractService, thContractMapper);

    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var entranceDate = formatter.format(LocalDate.now());
    var thContract =
        new ThContract(
            "L1",
            entranceDate,
            "-",
            "Salarié",
            BigDecimal.valueOf(2000),
            "Company",
            "Dev",
            "30",
            "key",
            "-");
    when(contractService.totalWorkDaysPerWorker()).thenReturn(Map.of(worker, List.of()));
    when(thContractMapper.toThContractsByWorker(any()))
        .thenReturn(Map.of(worker, List.of(thContract)));

    var result = service.generateCSV(null);

    assertNotNull(result);
    assertTrue(result.exists());
    var content = Files.readString(result.toPath());
    assertTrue(content.contains(",30,-,-"));
    result.delete();
  }
}
