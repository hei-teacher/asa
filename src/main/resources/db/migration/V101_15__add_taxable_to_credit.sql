-- distingue les rubriques qui entrent dans la base imposable (IRSA) des rubriques "non imposable"
-- (ex: Heures Supp. non Imposable 130%/150%) qui restent hors du calcul de l'IRSA.
ALTER TABLE credit ADD COLUMN taxable BOOLEAN NOT NULL DEFAULT true;

UPDATE credit SET taxable = false
WHERE credit_code IN ('HEURES_SUPP_NON_IMPOSABLE_130', 'HEURES_SUPP_NON_IMPOSABLE_150');
