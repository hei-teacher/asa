CREATE TYPE tax_side AS ENUM(
    'EMPLOYER',
    'EMPLOYEE'
);

CREATE TYPE tax_type AS ENUM(
    'PROPORTIONAL_TAX',
    'PROGRESSIVE_TAX'
);

CREATE TABLE IF NOT EXISTS tax (
  id  VARCHAR  PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  tax_type tax_type NOT NULL
);

CREATE TABLE IF NOT EXISTS tax_progression(
    id VARCHAR PRIMARY KEY,
  tax_id VARCHAR NOT NULL,
  min_amount NUMERIC(12,2) NOT NULL,
    max_amount NUMERIC(12,2) NOT NULL CHECK (min_amount < max_amount),
    rate NUMERIC(5,2) CHECK (rate BETWEEN 0 AND 100),
    "tax_side" tax_side NOT NULL,
    CONSTRAINT fk_progressive_tax foreign key (tax_id) references tax(id)
);


CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE tax_progression
    ADD CONSTRAINT no_overlap_ranges
    EXCLUDE USING gist (
     tax_id WITH =,
     tax_side WITH =,
     numrange(min_amount, max_amount, '[]') WITH &&
   );


CREATE INDEX idx_tax_progression_tax_id ON tax_progression(tax_id);
