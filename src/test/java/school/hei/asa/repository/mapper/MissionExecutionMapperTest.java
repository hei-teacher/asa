package school.hei.asa.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JMissionExecution;

class MissionExecutionMapperTest {

  private final MissionMapper missionMapper = mock(MissionMapper.class);
  private final WorkerMapper workerMapper = mock(WorkerMapper.class);
  private final MissionExecutionMapper mapper =
      new MissionExecutionMapper(missionMapper, workerMapper);

  @Test
  void toEntity() {
    var product = new Product("P1", "Product 1", "Desc");
    var mission = new Mission("M001", "Mission1", "Desc", 10, product);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
    var me = new MissionExecution(mission, worker, LocalDate.now(), 0.5, "comment", Instant.now());

    when(missionMapper.toEntity(mission))
        .thenReturn(new school.hei.asa.repository.model.JMission());
    var jWorker = new school.hei.asa.repository.model.JWorker();
    when(workerMapper.toEntity(worker, List.of())).thenReturn(jWorker);

    var result = mapper.toEntity(me);

    assertEquals(0.5, result.getDayPercentage());
    assertEquals("comment", result.getComment());
  }

  @Test
  void toDomain_with_cache() {
    var jme = new JMissionExecution();
    jme.setWorker_code("W-001");
    jme.setMission_code("M001");
    jme.setDate(Date.valueOf(LocalDate.now()));
    jme.setDayPercentage(0.5);
    jme.setComment("test");
    jme.setReportedAt(Instant.now());

    var product = new Product("P1", "P1", "Desc");
    var mission = new Mission("M001", "M1", "Desc", 10, product);
    var worker =
        new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

    when(missionMapper.toDomain(any(), any())).thenReturn(mission);
    when(workerMapper.toDomain(any(), any())).thenReturn(worker);

    var result = mapper.toDomain(List.of(jme));

    assertEquals(1, result.size());
    assertEquals("M001", result.getFirst().mission().code());
    assertEquals("W-001", result.getFirst().worker().code());
  }
}
