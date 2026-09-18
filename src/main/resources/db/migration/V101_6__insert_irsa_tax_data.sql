-- IRSA : impot progressif, uniquement a la charge du salarie (pas de part patronale)
-- source: https://portagesalarial-madagascar.com/irsa_madagascar_taxe_sur_le_salaire_impot_sur_le_revenu_salarial_et_assimiles.php
-- la tranche 0 - 350 000 Ar a un taux legal de 0%, mais un minimum forfaitaire de 3 000 Ar
-- s'applique en pratique : default_value = 3000 sur cette tranche.
INSERT INTO tax (id, "name", tax_type) VALUES
    ('IRSA', 'IRSA', 'PROGRESSIVE_TAX');

INSERT INTO tax_progression (id, tax_id, min_amount, max_amount, rate, tax_side, default_value) VALUES
    ('IRSA_BRACKET_1', 'IRSA', 0, 350000, 0, 'EMPLOYEE', 3000),
    ('IRSA_BRACKET_2', 'IRSA', 350000.01, 400000, 5, 'EMPLOYEE', 0),
    ('IRSA_BRACKET_3', 'IRSA', 400000.01, 500000, 10, 'EMPLOYEE', 0),
    ('IRSA_BRACKET_4', 'IRSA', 500000.01, 600000, 15, 'EMPLOYEE', 0),
    ('IRSA_BRACKET_5', 'IRSA', 600000.01, 4000000, 20, 'EMPLOYEE', 0),
    ('IRSA_BRACKET_6', 'IRSA', 4000000.01, 9999999999.99, 25, 'EMPLOYEE', 0);
