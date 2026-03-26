package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;

public class IsolatedThProductServiceTest extends FacadeIT {
  @Autowired private ThProductService thProductService;

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
  void filter_product_by_date_between() {
    var missionExecution1 =
        new ThMissionExecution(
            "mission0-code",
            "W-P-2024-01",
            LocalDate.parse("2024-07-01"),
            0.5,
            "comment3",
            false,
            true);
    var missionExecution2 =
        new ThMissionExecution(
            "mission0-code",
            "W-P-2024-01",
            LocalDate.parse("2024-07-01"),
            0.5,
            "comment4",
            false,
            true);
    var mission =
        new ThMission(
            "mission0-code",
            "a mission",
            "a description",
            List.of(missionExecution1, missionExecution2),
            false,
            false);
    var expected = new ThProduct("pCode0", "pname0", "pDescription0", List.of(mission), false);

    var actual =
        thProductService
            .filterThProductByWorkerCodeAndDateBetween(
                "W-P-2024-01", "2024-06-01", "2024-08-01", true)
            .stream()
            .filter(p -> p.code().equals("pCode0"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Test product not found"));

    assertEquals(expected, actual);
  }
}
