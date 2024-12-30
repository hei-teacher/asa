CREATE TABLE mission
(
    code                 VARCHAR NOT NULL,
    title                VARCHAR,
    description          VARCHAR,
    max_duration_in_days INTEGER,
    CONSTRAINT pk_mission PRIMARY KEY (code)
);

CREATE TABLE mission_execution
(
    id                   VARCHAR          NOT NULL,
    date                 date,
    mission_code         VARCHAR,
    worker_code          VARCHAR,
    execution_percentage DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_mission_execution PRIMARY KEY (id)
);

ALTER TABLE mission_execution
    ADD CONSTRAINT FK_MISSION_EXECUTION_ON_MISSION_CODE FOREIGN KEY (mission_code) REFERENCES mission (code);

ALTER TABLE mission_execution
    ADD CONSTRAINT FK_MISSION_EXECUTION_ON_WORKER_CODE FOREIGN KEY (worker_code) REFERENCES worker (code);