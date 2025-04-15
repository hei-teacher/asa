DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_type WHERE typname = 'level') THEN
        CREATE TYPE level AS ENUM ('L1', 'L2', 'L3', 'L4', 'L5', 'L6');
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS worker_level_history
(
    id                VARCHAR   NOT NULL,
    worker_code       VARCHAR   NOT NULL,
    level             level     NOT NULL,
    entrance_instant  TIMESTAMP NOT NULL,
    CONSTRAINT pk_worker_level_history PRIMARY KEY (id),
    CONSTRAINT fk_worker_level_history_on_worker_code FOREIGN KEY (worker_code) REFERENCES worker (code)
);