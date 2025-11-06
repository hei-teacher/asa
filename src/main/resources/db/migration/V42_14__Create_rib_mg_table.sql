CREATE TABLE IF NOT EXISTS rib_mg (
    id              VARCHAR NOT NULL,
    worker_code     VARCHAR,
    banque          VARCHAR,
    agence          VARCHAR,
    compte          VARCHAR,
    cle             VARCHAR,
    IBAN            VARCHAR,
    CONSTRAINT pk_rib_mg PRIMARY KEY (id)
);

ALTER TABLE rib_mg
    ADD CONSTRAINT FK_RIB_MG_ON_WORKER_CODE FOREIGN KEY (worker_code) REFERENCES worker (code);