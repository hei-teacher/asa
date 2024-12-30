CREATE TABLE IF NOT EXISTS product
(
    code        VARCHAR NOT NULL,
    name       VARCHAR,
    description VARCHAR,
    CONSTRAINT pk_product PRIMARY KEY (code)
);

ALTER TABLE mission
    ADD COLUMN product_code VARCHAR;
ALTER TABLE mission
    ADD CONSTRAINT FK_PRODUCT_CODE_ON_MISSION FOREIGN KEY (product_code) REFERENCES product (code);