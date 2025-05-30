package school.hei.asa.model;

public final class FullTimeEmployee extends Worker {
  public FullTimeEmployee(
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
