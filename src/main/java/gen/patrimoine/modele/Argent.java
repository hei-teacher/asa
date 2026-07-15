package gen.patrimoine.modele;

import static gen.patrimoine.modele.Devise.EUR;
import static gen.patrimoine.modele.Devise.MGA;
import static java.lang.Math.abs;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor
public class Argent implements Serializable {
  private final double montant;

  @Accessors(fluent = true)
  @Getter
  private final Devise devise;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Argent argent = (Argent) o;
    return Objects.equals(devise, argent.devise)
        && abs(montant - argent.montant) < 1;
  }

  @Override
  public int hashCode() {
    return Objects.hash(montant, devise);
  }

  public static Argent euro(double montant) {
    return new Argent(montant, EUR);
  }

  public static Argent ariary(int montant) {
    return new Argent(montant, MGA);
  }

  public Argent convertir(Devise autreDevise, LocalDate t) {
    return new Argent(
        (montant * devise.valeurEnAriary(t)) / autreDevise.valeurEnAriary(t), autreDevise);
  }

  public Argent mult(double d) {
    return new Argent(montant * d, devise);
  }

  public Argent minus(Argent that, LocalDate t) {
    return new Argent(montant - that.convertir(devise, t).montant, devise);
  }

  public Argent add(Argent that, LocalDate t) {
    return new Argent(montant + that.convertir(devise, t).montant, devise);
  }

  public boolean gt(double n) {
    return montant > n;
  }

  public boolean lt(double n) {
    return montant < n;
  }

  public boolean le(double n) {
    return montant <= n;
  }

  public String ppMontant() {
    return montant + "";
  }

  public boolean hasSameValeurComptable(Argent that, LocalDate t) {
    return that.convertir(devise, t).equals(this);
  }
}
