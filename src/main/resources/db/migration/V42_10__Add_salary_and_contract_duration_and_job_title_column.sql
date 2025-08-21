ALTER TABLE worker_level_history
    ADD COLUMN IF NOT EXISTS salary NUMERIC(15,2);

ALTER TABLE worker_level_history
    ADD COLUMN IF NOT EXISTS job_title VARCHAR;

ALTER TABLE worker_level_history
    ADD COLUMN IF NOT EXISTS contract_duration INT;