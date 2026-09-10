-- 9000/9500 : lignes "autres" non soumises a cotisation ni imposables, variables au mois le mois.
-- neutres par defaut (rate=0). AUTRES_RETENUES_NON_SOUMISES est une retenue : quand une valeur
-- reelle sera saisie, son taux (deja autorise en negatif par la contrainte de V101_11) doit l'etre.
INSERT INTO credit (id, credit_code, "name", divisor, rate) VALUES
    ('AUTRES_GAINS_NON_SOUMIS', 'AUTRES_GAINS_NON_SOUMIS', 'Autres gains ni soumis cotis. ni imposable', 1, 0),
    ('AUTRES_RETENUES_NON_SOUMISES', 'AUTRES_RETENUES_NON_SOUMISES', 'Autres retenues ni soumises cotis. ni imposable', 1, 0);
