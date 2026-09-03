package school.hei.asa.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Tax {
  private final String id;
  private final String name;

  public abstract TaxAmount resolve(BigDecimal base);
}
