package school.hei.asa.model;

public record BankAccount(
    String bank, String agency, String account, String key, String iban, Worker worker) {

  @Override
  public String toString() {
    return String.format("%s-%s-%s-%s", bank, agency, account, key);
  }
}
