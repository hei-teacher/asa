package school.hei.asa.model;

import java.math.BigDecimal;

public record TaxAmount(
    BigDecimal employerContributionValue, BigDecimal employeeContributionValue) {}
