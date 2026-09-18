-- credit_code devient la cle metier du credit (stable), independante de son id technique
ALTER TABLE credit ADD COLUMN credit_code VARCHAR;
UPDATE credit SET credit_code = id;
ALTER TABLE credit ALTER COLUMN credit_code SET NOT NULL;
ALTER TABLE credit ADD CONSTRAINT uq_credit_credit_code UNIQUE (credit_code);

-- number_of_units n'est pas une propriete du credit mais une donnee mensuelle par salarie :
-- elle est deplacee dans earned_credits
ALTER TABLE credit DROP COLUMN number_of_units;

CREATE TABLE IF NOT EXISTS earned_credits (
  id VARCHAR PRIMARY KEY,
  credit_code VARCHAR NOT NULL REFERENCES credit(credit_code),
  number_of_units NUMERIC(12,2) NOT NULL,
  year_month VARCHAR NOT NULL,
  worker_code VARCHAR REFERENCES worker(code)
);

CREATE INDEX idx_earned_credits_credit_code ON earned_credits(credit_code);
CREATE INDEX idx_earned_credits_worker_code ON earned_credits(worker_code);
