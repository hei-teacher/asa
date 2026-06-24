CREATE TABLE IF NOT EXISTS app_settings (
    id                            VARCHAR NOT NULL,
    low_contract_days_threshold   INTEGER NOT NULL DEFAULT 10,
    CONSTRAINT pk_app_settings PRIMARY KEY (id)
);

INSERT INTO app_settings (id, low_contract_days_threshold)
VALUES ('DEFAULT', 10)
ON CONFLICT (id) DO NOTHING;
