ALTER TABLE contract
    ADD COLUMN new_level VARCHAR;

UPDATE contract
    SET new_level = (
        SELECT contract_level.code
        FROM contract_level
        WHERE contract_level.level_id = contract.level
    );

ALTER TABLE contract
    DROP CONSTRAINT fk_worker_level_history_on_level;

ALTER TABLE contract
    ADD CONSTRAINT fk_contract_on_level
        FOREIGN KEY (new_level) REFERENCES contract_level (code);

ALTER TABLE contract
    DROP COLUMN level;

ALTER TABLE contract
    RENAME COLUMN new_level to level;

ALTER TABLE contract_level
    DROP CONSTRAINT pk_worker_level;

ALTER TABLE contract_level
    ADD CONSTRAINT pk_worker_level PRIMARY KEY (code);

ALTER TABLE contract_level
    DROP COLUMN level_id;
