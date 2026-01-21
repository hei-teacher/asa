package school.hei.asa.service;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;

@Transactional
class MissionServiceIT extends FacadeIT {

  @Autowired MissionService missionService;

  @Test
  void thProductsExecutedDays_count_by_month() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission("code1", "mission1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission("code2", "mission2", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission("code3", "mission3", "description", List.of(missionExecution3), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false));

    var thProducsByMonth = missionService.thProductsExecutedDaysSumByMonth(thProducts, true);

    var januaryExecutedDays = thProducsByMonth.get("january");
    var februaryExecutedDays = thProducsByMonth.get("february");
    assertEquals(0.5, januaryExecutedDays);
    assertEquals(1.5, februaryExecutedDays);
  }

  @Test
  void filter_ThMissions() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var thMissions = missionService.getUniqueMissionsByTitle(thProducts);

    assertEquals(4, thMissions.size());
  }

  @Test
  void filter_ThMissions_with_same_title() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var thMissions = missionService.getAllMissionsFromProducts(thProducts);

    assertEquals(2, thMissions.size());
  }

  @Test
  void group_product_by_month() {
    var missionExecution1 =
        new ThMissionExecution(
            "me1", "worker", LocalDate.parse("2025-01-15"), 0.5, "comment", false, false);
    var missionExecution2 =
        new ThMissionExecution(
            "me2", "worker", LocalDate.parse("2025-02-15"), 0.5, "comment", false, false);
    var missionExecution3 =
        new ThMissionExecution(
            "me3", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var missionExecution4 =
        new ThMissionExecution(
            "me4", "worker", LocalDate.parse("2025-02-20"), 1, "comment", false, false);
    var mission1 =
        new ThMission(
            "code1", "mission-test1", "description", List.of(missionExecution1), false, false);
    var mission2 =
        new ThMission(
            "code2", "mission-test1", "description", List.of(missionExecution2), false, false);
    var mission3 =
        new ThMission(
            "code3", "mission-test2", "description", List.of(missionExecution3), false, false);
    var mission4 =
        new ThMission(
            "code4", "mission-test2", "description", List.of(missionExecution4), false, false);
    var thProducts =
        List.of(
            new ThProduct("code1", "product1", "description", List.of(mission1), false),
            new ThProduct("code2", "product2", "description", List.of(mission2), false),
            new ThProduct("code3", "product3", "description", List.of(mission3), false),
            new ThProduct("code4", "product4", "description", List.of(mission4), false));

    var result = missionService.thProductsByMonth(thProducts).get("february");

    assertEquals(0, result.getFirst().missions().getFirst().getMissionExecutions().size());
    assertEquals(1, result.getLast().missions().getFirst().getMissionExecutions().size());
  }

  @Test
  @SneakyThrows
  void export_contract_for_one_worker() {
    var actualCSV = missionService.generateCSV("W-P-2024-01");

    var actualContent = Files.readString(actualCSV.toPath());

    var expectedCSV = expectedFile();
    var expectedContent = Files.readString(expectedCSV.toPath());

    assertEquals(expectedContent, actualContent);
  }

  private File expectedFile() {
    String filePath = System.getProperty("java.io.tmpdir");
    File file = new File(filePath, "test.csv");
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(
          "code,worker,contract level,start date,"
              + "contract duration (in days),"
              + "total days worked,remaining days"
              + lineSeparator());
      fileWriter.flush();
      fileWriter.write(
          String.format("W-P-2024-01,Lita Andria,L4P-2026,2025-01-01,80,-,-" + lineSeparator()));
      fileWriter.write(
          String.format("W-P-2024-01,Lita Andria,L5,2023-01-01,13,2.0,11.0" + lineSeparator()));
      fileWriter.flush();
      return file;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
