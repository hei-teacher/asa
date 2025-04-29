ALTER TABLE worker_level_history
    DROP COLUMN IF EXISTS level;

DO $$
BEGIN
    IF EXISTS (SELECT FROM pg_type WHERE typname = 'level') THEN
        DROP TYPE level;
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS worker_level
(
    id VARCHAR NOT NULL,
    level VARCHAR NOT NULL UNIQUE,
    CONSTRAINT pk_worker_level PRIMARY KEY (id)
);

ALTER TABLE worker
    ADD COLUMN IF NOT EXISTS level VARCHAR;

ALTER TABLE worker
    ADD CONSTRAINT fk_worker_level
        FOREIGN KEY (level) REFERENCES worker_level (id);
