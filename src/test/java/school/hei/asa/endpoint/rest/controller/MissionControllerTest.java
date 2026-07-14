package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.endpoint.rest.service.ThContractService;
import school.hei.asa.endpoint.rest.service.ThMissionService;
import school.hei.asa.endpoint.rest.service.ThProductService;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.SensitiveWorkerFilter;

class MissionControllerTest {

  private final DailyExecutionRepository dailyExecutionRepository =
      mock(DailyExecutionRepository.class);
  private final CareProductCodeSupplier careProductCodeSupplier =
      mock(CareProductCodeSupplier.class);
  private final ThDailyExecutionMapper thDailyExecutionMapper = mock(ThDailyExecutionMapper.class);
  private final WorkerToModelAdder workerToModelAdder = mock(WorkerToModelAdder.class);
  private final ThMissionService thMissionService = mock(ThMissionService.class);
  private final ThProductService thProductService = mock(ThProductService.class);
  private final ThContractService thContractService = mock(ThContractService.class);
  private final SensitiveWorkerFilter sensitiveWorkerFilter = mock(SensitiveWorkerFilter.class);
  private final WorkerFromAuthentication workerFromAuthentication =
      mock(WorkerFromAuthentication.class);

  private final MissionController controller =
      new MissionController(
          dailyExecutionRepository,
          careProductCodeSupplier,
          thDailyExecutionMapper,
          workerToModelAdder,
          thMissionService,
          thProductService,
          thContractService,
          sensitiveWorkerFilter,
          workerFromAuthentication);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");

  @Test
  void getMissions_returns_missions_view_with_model_attributes() {
    var authentication = mock(Authentication.class);
    var model = new ConcurrentModel();
    var thProducts = List.of(new ThProduct("P1", "P1", "Desc", List.of(), false));
    var thMissions = List.of(new ThMission("M1", "Title1", "Desc", List.of(), false, false));
    var chartData = List.<Map<String, Object>>of();
    var months = Map.<String, List<ThProduct>>of();
    var total = Map.<String, Double>of();

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(thProductService.filterThProductByWorkerCodeAndDateBetween(
            any(), any(), any(), anyBoolean()))
        .thenReturn(thProducts);
    when(thProductService.toProductChartData(thProducts)).thenReturn(chartData);
    when(thProductService.thProductsByMonth(thProducts)).thenReturn(months);
    when(thMissionService.getUniqueMissionsByTitle(thProducts)).thenReturn(thMissions);
    when(thMissionService.toMissionChartData(anyList())).thenReturn(chartData);
    when(thMissionService.getAllMissionsFromProducts(thProducts)).thenReturn(thMissions);
    when(thProductService.thProductsExecutedDaysSumByMonth(thProducts, true)).thenReturn(total);
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), any(Model.class)))
        .thenReturn(worker);

    var viewName = controller.getMissions(model, null, null, null, authentication);

    assertEquals("missions", viewName);
    assertNull(model.getAttribute("workerCode"));
    assertNull(model.getAttribute("startDate"));
    assertNull(model.getAttribute("endDate"));
    assertSame(thProducts, model.getAttribute("products"));
    assertSame(chartData, model.getAttribute("executedDaysByProduct"));
    assertSame(months, model.getAttribute("months"));
    assertSame(chartData, model.getAttribute("executedDaysByProductMission"));
    assertSame(chartData, model.getAttribute("executedDaysByMission"));
    assertSame(total, model.getAttribute("total"));
    verify(workerToModelAdder).apply(any(WorkerModelAdderParam.class), any(Model.class));
  }

  @Test
  void getMissionExecutions_returns_mission_executions_view() {
    var authentication = mock(Authentication.class);
    var model = new ConcurrentModel();
    var now = YearMonth.now();

    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(dailyExecutionRepository.findByDateBetween(now.atDay(1), now.atEndOfMonth()))
        .thenReturn(List.of());
    when(careProductCodeSupplier.get()).thenReturn("CARE-001");
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), any(Model.class)))
        .thenReturn(worker);

    var viewName = controller.getMissionExecutions(model, null, null, authentication);

    assertEquals("mission-executions", viewName);
    assertNotNull(model.getAttribute("dailyExecutions"));
    assertTrue(((List<?>) model.getAttribute("dailyExecutions")).isEmpty());
    assertEquals("CARE-001", model.getAttribute("careProductCode"));
    assertEquals(now.toString(), model.getAttribute("yearMonth"));
    assertEquals(now.getYear(), model.getAttribute("year"));
    assertNull(model.getAttribute("workerCode"));
    verify(workerToModelAdder).apply(any(WorkerModelAdderParam.class), any(Model.class));
  }

  @Test
  void exportToCSV_returns_csv_file_as_attachment() throws Exception {
    var tempFile = File.createTempFile("export-", ".csv");
    tempFile.deleteOnExit();
    Files.writeString(tempFile.toPath(), "code,worker\nW-001,John\n");

    when(thContractService.generateCSV("W-001")).thenReturn(tempFile);

    ResponseEntity<ByteArrayResource> response = controller.exportToCSV("W-001");

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(
        MediaType.parseMediaType("application/octet-stream"),
        response.getHeaders().getContentType());
    assertTrue(
        response
            .getHeaders()
            .getFirst(HttpHeaders.CONTENT_DISPOSITION)
            .contains("attachment; filename="));
    assertNotNull(response.getBody());
    assertEquals(tempFile.length(), response.getHeaders().getContentLength());
  }
}
