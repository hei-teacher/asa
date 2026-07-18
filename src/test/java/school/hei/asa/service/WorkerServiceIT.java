package school.hei.asa.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Worker;

public class WorkerServiceIT extends FacadeIT {
  @Autowired WorkerService workerService;

  Model modelWithYearAttribute;
  Model modelWithStartAndEndDateAttribute;

  @BeforeEach
  void setUp() {
    modelWithYearAttribute = mock(Model.class);
    modelWithStartAndEndDateAttribute = mock(Model.class);

    when(modelWithYearAttribute.getAttribute("year")).thenReturn(2026);
    when(modelWithStartAndEndDateAttribute.getAttribute("startDate")).thenReturn("2024-01-01");
    when(modelWithStartAndEndDateAttribute.getAttribute("endDate")).thenReturn("2024-12-31");
  }

  @Test
  void can_get_worker_from_year() {
    var expected = workersFromYear();
    var actual = workerService.getWorkersFrom(modelWithYearAttribute);

    assertTrue(
        actual.containsAll(expected),
        "Expected workers " + expected + " to be present in " + actual);
  }

  @Test
  void can_get_worker_from_date_range() {
    var expected = workersFromDateRange();
    var actual = workerService.getWorkersFrom(modelWithStartAndEndDateAttribute);

    assertTrue(
        actual.containsAll(expected),
        "Expected workers " + expected + " to be present in " + actual);
  }

  private List<Worker> workersFromYear() {
    var worker = new Worker("W-P-2024-01", "Lita Andria", null, null, null, null, null, null);
    return List.of(worker);
  }

  private List<Worker> workersFromDateRange() {
    var worker1 = new Worker("W-101", "John", null, null, null, null, null, null);
    var worker2 = new Worker("W-P-2024-01", "Lita Andria", null, null, null, null, null, null);

    return List.of(worker1, worker2);
  }
}
