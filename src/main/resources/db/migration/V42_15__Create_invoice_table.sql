CREATE TABLE IF NOT EXISTS invoice (
    id                 VARCHAR NOT NULL,
    worker_code        VARCHAR,
    year_month         VARCHAR NOT NULL,
    invoice_reference  VARCHAR NOT NULL,

    CONSTRAINT pk_invoice PRIMARY KEY (id)
);

ALTER TABLE invoice
    ADD CONSTRAINT FK_INVOICE_ON_WORKER_CODE FOREIGN KEY (worker_code) REFERENCES worker (code);