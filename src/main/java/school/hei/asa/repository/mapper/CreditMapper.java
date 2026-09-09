package school.hei.asa.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.asa.model.Credit;
import school.hei.asa.repository.model.JCredit;

@Component
public class CreditMapper {

  public Credit toDomain(JCredit jCredit) {
    return new Credit(
        jCredit.getId(),
        jCredit.getName(),
        jCredit.getDivisor(),
        jCredit.getRate(),
        jCredit.getNumberOfUnits());
  }
}
