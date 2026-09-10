package school.hei.asa.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Credit {
  private String id;
  private String creditCode;
  private String name;
  private Double divisor;
  private Double rate;
  private Boolean taxable;
}
