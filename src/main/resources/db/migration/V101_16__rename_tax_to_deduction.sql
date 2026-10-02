ALTER TABLE tax RENAME TO deduction;

CREATE TYPE deduction_type AS ENUM ('TAX', 'DEDUCTION');

ALTER TABLE deduction ADD COLUMN type deduction_type NOT NULL DEFAULT 'TAX';
ALTER TABLE deduction ALTER COLUMN type DROP DEFAULT;

UPDATE deduction SET type = 'DEDUCTION' WHERE id = 'IRSA';
