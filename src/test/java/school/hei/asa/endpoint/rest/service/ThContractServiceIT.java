package school.hei.asa.endpoint.rest.service;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;

class ThContractServiceIT extends FacadeIT {
  @Autowired ThContractService thContractService;

  @Test
  @SneakyThrows
  void export_contract_for_one_worker() {
    var actualCSV = thContractService.generateCSV("W-P-2024-01");

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
