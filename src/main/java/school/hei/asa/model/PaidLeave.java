package school.hei.asa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaidLeave {
  private Double base;
  private Double taken;
  private Double notTakenTheLastMonth;
  private Double earnedPaidLeave;

  public Double getLeft() {
    return notTakenTheLastMonth + base - taken;
  }
}
