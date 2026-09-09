package school.hei.asa.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PaidLeave {
  private Double base;
  private Double taken;
  private Double notTakenTheLastMonth;

  public Double getLeft() {
    return notTakenTheLastMonth + base - taken;
  }
}
