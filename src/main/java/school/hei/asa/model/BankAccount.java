package school.hei.asa.model;

public record BankAccount(
    String bank, String agency, String account, String key, String iban, Worker worker) {

  @Override
  public String toString() {
    return String.format(
        "Banque: %s, Agence: %s, Compte: %s, Clé: %s, IBAN: %s", bank, agency, account, key, iban);
  }
}
