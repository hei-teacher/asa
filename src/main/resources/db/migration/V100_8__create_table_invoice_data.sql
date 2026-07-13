CREATE TABLE invoice_data (
                         id VARCHAR PRIMARY KEY,
                         year_month VARCHAR,
                         reference_date DATE,
                         issue_date DATE,
                         description TEXT,
                         unit_price NUMERIC(19,2),
                         amount NUMERIC(19,2),
                         has_upgraded_level BOOLEAN,
                         extra_description TEXT,
                         extra_quantity DOUBLE PRECISION,
                         extra_unit_price NUMERIC(19,2),
                         extra_amount NUMERIC(19,2),
                         total NUMERIC(19,2),
                         parsed_amount TEXT,
                         rib TEXT,
                         invoice_id VARCHAR,
                         CONSTRAINT fk_invoice_reference
                             FOREIGN KEY (invoice_id)
                                 REFERENCES invoice(id)
);