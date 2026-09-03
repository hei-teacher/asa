package school.hei.asa.model;

import java.math.BigDecimal;

public record TaxProgression(
    Double rate, TaxSide taxSide, BigDecimal minAmount, BigDecimal maxAmount) {}
