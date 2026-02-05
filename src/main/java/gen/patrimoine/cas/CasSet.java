package gen.patrimoine.cas;

import gen.patrimoine.modele.Argent;
import java.util.Set;

public record CasSet(Set<Cas> set, Argent objectifFinal) {}
