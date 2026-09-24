package school.hei.asa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaidLeave {
  private Double base;
  private Double taken;
  private Double leftTheMonthBefore;
  private Double earnedPaidLeave;

  public Double getLeft() {
    return leftTheMonthBefore + base - taken;
  }
}
