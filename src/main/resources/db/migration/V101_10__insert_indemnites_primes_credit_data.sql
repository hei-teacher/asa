-- lignes "divers" du bulletin : pas de formule connue pour l'instant (taux 0 = neutre).
-- La ligne existe deja pour pouvoir etre rattachee via earned_credits, mais un taux de 0%
-- garantit qu'elle ne change rien au total tant qu'aucune regle de calcul reelle n'est fournie.
INSERT INTO credit (id, credit_code, "name", divisor, rate) VALUES
    ('INDEMNITES_DIVERSES', 'INDEMNITES_DIVERSES', 'Indemnités diverses', 1, 0),
    ('PRIMES_DIVERS', 'PRIMES_DIVERS', 'Primes divers', 1, 0);
