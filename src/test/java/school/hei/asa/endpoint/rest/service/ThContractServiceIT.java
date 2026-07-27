package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.nio.file.Files;
import java.time.Instant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.repository.model.JContract;
import school.hei.asa.repository.model.JContractLevel;
import school.hei.asa.repository.model.JWorker;

@Slf4j
class ThContractServiceIT extends FacadeIT {
  @Autowired ThContractService thContractService;
  @Autowired EntityManager entityManager;
  @Autowired TransactionTemplate transactionTemplate;

  @BeforeEach
  void setupRequestContext() {
    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));
    addL5Contract();
  }

  private void addL5Contract() {
    transactionTemplate.execute(status -> {
      var jWorker = entityManager.find(JWorker.class, "W-P-2024-01");
      var jContractLevel = entityManager.find(JContractLevel.class, "L5");
      var jContract = new JContract();
      jContract.setId("wlh_id_0");
      jContract.setWorker(jWorker);
      jContract.setLevel(jContractLevel);
      jContract.setEntranceInstant(Instant.parse("2023-01-01T08:00:00Z"));
      jContract.setDurationInDays(13);
      jContract.setJobTitle("job_title");
      jContract.setContractBucketKey("contract_bucket_key");
      entityManager.persist(jContract);
      return null;
    });
  }

  @AfterEach
  void resetRequestContext() {
    RequestContextHolder.resetRequestAttributes();
    transactionTemplate.execute(status -> {
      entityManager.createQuery("delete from JContract where id = 'wlh_id_0'")
          .executeUpdate();
      return null;
    });
  }

  @Test
  @SneakyThrows
  void export_contract_for_one_worker() {
    var actualCSV = thContractService.generateCSV("W-P-2024-01");

    var actualContent = Files.readString(actualCSV.toPath());

    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L5,01 Jan 2023,13,2.0,11.0"));
    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L4P-2026,01 Jan 2025,80,-,-"));
  }

  @Test
  @SneakyThrows
  void export_contract_for_all_workers() {
    var actualCSV = thContractService.generateCSV(null);

    var actualContent = Files.readString(actualCSV.toPath());
    log.info("here is the content of the file: {}", actualContent);

    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L5,01 Jan 2023,13,2.0,11.0"));
    assertTrue(actualContent.contains("W-P-2024-01,Lita Andria,L4P-2026,01 Jan 2025,80,-,-"));
  }
}
