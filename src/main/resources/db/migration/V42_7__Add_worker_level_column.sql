ALTER TABLE worker
    DROP COLUMN IF EXISTS level;

ALTER TABLE worker_level_history
    ADD COLUMN IF NOT EXISTS level VARCHAR;

ALTER TABLE worker_level_history
    ADD CONSTRAINT fk_worker_level_history_on_level
        FOREIGN KEY (level) REFERENCES worker_level (id);
