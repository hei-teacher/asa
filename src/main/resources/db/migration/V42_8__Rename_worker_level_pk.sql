ALTER TABLE worker_level
    ADD COLUMN level_id VARCHAR;

ALTER TABLE worker_level_history
    DROP CONSTRAINT fk_worker_level_history_on_level;

ALTER TABLE worker_level
    DROP CONSTRAINT pk_worker_level;

ALTER TABLE worker_level
    ADD CONSTRAINT pk_worker_level PRIMARY KEY (level_id);

ALTER TABLE worker_level
    DROP COLUMN id;

ALTER TABLE worker_level_history
    ADD CONSTRAINT fk_worker_level_history_on_level
        FOREIGN KEY (level) REFERENCES worker_level (level_id);