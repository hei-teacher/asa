package school.hei.asa.model;

public sealed class Contractor extends Worker permits StudentContractor, PartnerContractor {
  public Contractor(
      String code,
      String name,
      String email,
      String fullname,
      String address,
      String city,
      String nif,
      String stat) {
    super(code, name, email, fullname, address, city, nif, stat);
  }
}
