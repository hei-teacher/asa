package school.hei.asa.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.asa.model.Tax;
import school.hei.asa.model.TaxProgression;
import school.hei.asa.repository.model.JTax;
import school.hei.asa.repository.model.JTaxProgression;

@Component
public class TaxMapper {

  public Tax toDomain(JTax jTax) {
    return new Tax(
        jTax.getId(),
        jTax.getName(),
        jTax.getTaxProgressions().stream().map(this::toDomain).toList());
  }

  private TaxProgression toDomain(JTaxProgression jTaxProgression) {
    return new TaxProgression(
        jTaxProgression.getRate(),
        jTaxProgression.getTaxSide(),
        jTaxProgression.getMinAmount(),
        jTaxProgression.getMaxAmount(),
        jTaxProgression.getDefaultValue());
  }
}
