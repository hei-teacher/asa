-- taxes proportionnelles : une seule tranche par côté, de 0 à un plafond arbitrairement haut
INSERT INTO tax (id, "name", tax_type) VALUES
    ('CNAPS', 'CNAPS', 'PROPORTIONAL_TAX'),
    ('OSTIE', 'OSTIE', 'PROPORTIONAL_TAX'),
    ('FMFP', 'FMFP', 'PROPORTIONAL_TAX');

INSERT INTO tax_progression (id, tax_id, min_amount, max_amount, rate, tax_side) VALUES
    ('CNAPS_EMPLOYEE', 'CNAPS', 0, 9999999999.99, 1, 'EMPLOYEE'),
    ('CNAPS_EMPLOYER', 'CNAPS', 0, 9999999999.99, 13, 'EMPLOYER'),
    ('OSTIE_EMPLOYEE', 'OSTIE', 0, 9999999999.99, 1, 'EMPLOYEE'),
    ('OSTIE_EMPLOYER', 'OSTIE', 0, 9999999999.99, 5, 'EMPLOYER'),
    ('FMFP_EMPLOYER', 'FMFP', 0, 9999999999.99, 1, 'EMPLOYER');
