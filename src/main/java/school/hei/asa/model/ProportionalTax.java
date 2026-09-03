package school.hei.asa.model;

import static school.hei.asa.number.NullToBigDecimalHanlder.calculatePercentageValue;

import java.math.BigDecimal;

public class ProportionalTax extends Tax {
  private final Double employerContributionPercentage;
  private final Double employeeContributionPercentage;

  public ProportionalTax(
      String id, String name, Double employerContribution, Double employeeContributionPercentage) {
    super(id, name);
    this.employerContributionPercentage = employerContribution;
    this.employeeContributionPercentage = employeeContributionPercentage;
  }

  @Override
  public TaxAmount resolve(BigDecimal base) {
    var employerContributionAmount = calculatePercentageValue(employerContributionPercentage, base);
    var employeeContributionAmount = calculatePercentageValue(employeeContributionPercentage, base);

    return new TaxAmount(employerContributionAmount, employeeContributionAmount);
  }
}
