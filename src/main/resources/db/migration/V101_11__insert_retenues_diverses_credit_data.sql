-- Retenues diverses (1400) change tous les mois (comme Absence/Conge/Heures supp) : c'est un
-- credit rattache via earned_credits, pas une taxe fixe basee sur un pourcentage du salaire.
-- Comme c'est une retenue (deduction), son taux doit pouvoir etre negatif
-- (contrainte deja elargie en V101_7_1).

-- neutre par defaut (rate=0) tant qu'aucune retenue reelle n'est saisie pour un mois donne ;
-- quand elle l'est, le taux (ou le earned_credits associe) doit etre negatif.
INSERT INTO credit (id, credit_code, "name", divisor, rate) VALUES
    ('RETENUES_DIVERSES', 'RETENUES_DIVERSES', 'Retenues diverses', 1, 0);
