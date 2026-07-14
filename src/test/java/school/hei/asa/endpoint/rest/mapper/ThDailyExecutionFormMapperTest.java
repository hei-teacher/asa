package school.hei.asa.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.model.Mission;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.MissionRepository;

class ThDailyExecutionFormMapperTest {

  private final MissionRepository missionRepository = mock(MissionRepository.class);
  private final ThDailyExecutionFormMapper mapper =
      new ThDailyExecutionFormMapper(missionRepository);

  @Test
  void toDomain_with_full_data() {
    var product = new Product("P1", "Product 1", "Desc");
    var mission = new Mission("M001", "Mission1", "Desc", 10, product);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

    when(missionRepository.findByCode(eq("M001"))).thenReturn(Optional.of(mission));

    var form =
        new ThDailyExecutionForm(
            "2025-01-15",
            "M001",
            "1.0",
            "comment1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "");

    var result = mapper.toDomain(form, worker);

    assertEquals(worker, result.worker());
    assertEquals("2025-01-15", result.date().toString());
    assertEquals(1, result.executions().size());
    assertEquals(1.0, result.executions().getFirst().dayPercentage());
  }

  @Test
  void toDomain_without_date_throws() {
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var form =
        new ThDailyExecutionForm("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

    assertThrows(IllegalArgumentException.class, () -> mapper.toDomain(form, worker));
  }

  @Test
  void toDomain_with_comment_blank_throws() {
    var product = new Product("P1", "Product 1", "Desc");
    var mission = new Mission("M001", "Mission1", "Desc", 10, product);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

    when(missionRepository.findByCode(eq("M001"))).thenReturn(Optional.of(mission));

    var form =
        new ThDailyExecutionForm(
            "2025-01-15", "M001", "0.5", "", "", "", "", "", "", "", "", "", "", "", "", "");

    assertThrows(IllegalArgumentException.class, () -> mapper.toDomain(form, worker));
  }

  @Test
  void toDomain_with_all_five_missions() {
    var product = new Product("P1", "Product 1", "Desc");
    var m1 = new Mission("M001", "M1", "Desc", 10, product);
    var m2 = new Mission("M002", "M2", "Desc", 10, product);
    var m3 = new Mission("M003", "M3", "Desc", 10, product);
    var m4 = new Mission("M004", "M4", "Desc", 10, product);
    var m5 = new Mission("M005", "M5", "Desc", 10, product);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

    when(missionRepository.findByCode(eq("M001"))).thenReturn(Optional.of(m1));
    when(missionRepository.findByCode(eq("M002"))).thenReturn(Optional.of(m2));
    when(missionRepository.findByCode(eq("M003"))).thenReturn(Optional.of(m3));
    when(missionRepository.findByCode(eq("M004"))).thenReturn(Optional.of(m4));
    when(missionRepository.findByCode(eq("M005"))).thenReturn(Optional.of(m5));

    var form =
        new ThDailyExecutionForm(
            "2025-01-15",
            "M001",
            "0.2",
            "c1",
            "M002",
            "0.2",
            "c2",
            "M003",
            "0.2",
            "c3",
            "M004",
            "0.2",
            "c4",
            "M005",
            "0.2",
            "c5");

    var result = mapper.toDomain(form, worker);

    assertEquals(5, result.executions().size());
  }
}
