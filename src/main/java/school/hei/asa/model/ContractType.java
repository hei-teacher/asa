package school.hei.asa.model;

public enum ContractType {
  STUDENT_CONTRACTOR("studentContractor"),
  PARTNER_CONTRACTOR("partnerContractor"),
  FULL_TIME_EMPLOYEE("fullTimeEmployee");

  private final String value;

  ContractType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
