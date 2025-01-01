package school.hei.asa.model;

public sealed class Contractor extends Worker permits StudentContractor, PartnerContractor {
  public Contractor(String code, String name, String email) {
    super(code, name, email);
  }
}
