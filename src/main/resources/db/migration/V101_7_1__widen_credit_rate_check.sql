ALTER TABLE credit DROP CONSTRAINT credit_rate_check;
ALTER TABLE credit ADD CONSTRAINT credit_rate_check CHECK (rate BETWEEN -100 AND 300);
