ALTER TABLE contract DROP COLUMN contract_type;
ALTER TABLE contract_level
    ADD COLUMN IF NOT EXISTS type VARCHAR;
ALTER TABLE contract_level
    ADD COLUMN IF NOT EXISTS monthly_pay DOUBLE PRECISION;
ALTER TABLE contract_level
    ADD COLUMN IF NOT EXISTS daily_pay DOUBLE PRECISION;
ALTER TABLE contract_level RENAME COLUMN level TO code;

ALTER TABLE contract RENAME COLUMN  total_work_days TO duration_in_days;
ALTER TABLE contract DROP COLUMN  contract_duration;