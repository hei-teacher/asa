package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import school.hei.asa.conf.FacadeIT;

@Slf4j
class ThContractServiceIT extends FacadeIT {
  @Autowired ThContractService thContractService;

  @BeforeEach
  void setupRequestContext() {
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @AfterEach
  void resetRequestContext() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @SneakyThrows
  void export_contract_for_one_worker() {
    var actualCSV = thContractService.generateCSV("W-P-2024-01");

    var actualContent = Files.readString(actualCSV.toPath());

    log.info("here is the content of the file: {}", actualContent);

    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L5,01 Jan 2023,13,2.0,11.0"));
    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L4P-2026,01 Jan 2025,80,-,-"));
  }

  @Test
  @SneakyThrows
  void export_contract_for_all_workers() {
    var actualCSV = thContractService.generateCSV(null);

    var actualContent = Files.readString(actualCSV.toPath());

    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L5,01 Jan 2023,13,2.0,11.0"));
    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L4P-2026,01 Jan 2025,80,-,-"));
  }
}
