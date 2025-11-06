package school.hei.asa.model;

public record BankAccount(
        String bank,
        String agency,
        String account,
        String key,
        String iban,
        Worker worker
        ) {
}
