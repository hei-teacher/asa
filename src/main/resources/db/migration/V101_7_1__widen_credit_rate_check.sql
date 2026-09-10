-- les heures supp/majorees appliquent des taux > 100% (130%, 150%...) et certaines retenues
-- doivent pouvoir etre negatives : la contrainte d'origine (0-100) est trop stricte.
ALTER TABLE credit DROP CONSTRAINT credit_rate_check;
ALTER TABLE credit ADD CONSTRAINT credit_rate_check CHECK (rate BETWEEN -100 AND 300);
