package school.hei.asa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;

class CreditRepositoryTest extends FacadeIT {
  @Autowired CreditRepository creditRepository;

  @Test
  void find_all_returns_the_reference_credits() {
    var actual = creditRepository.findAll();

    assertTrue(actual.stream().anyMatch(credit -> credit.getCreditCode().equals("ABSENCE")));
  }

  @Test
  void find_by_id_returns_a_single_credit() {
    var actual = creditRepository.findById("ABSENCE");

    assertTrue(actual.isPresent());
    assertEquals("ABSENCE", actual.get().getCreditCode());
  }
}
