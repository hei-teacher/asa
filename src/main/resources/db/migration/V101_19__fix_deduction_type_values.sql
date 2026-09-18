-- Les valeurs posees en V101_16 etaient inversees : IRSA est un impot (TAX), CNAPS/OSTIE/FMFP
-- sont des cotisations sociales retenues a la source (DEDUCTION).
UPDATE deduction SET type = 'DEDUCTION' WHERE id IN ('CNAPS', 'OSTIE', 'FMFP');
UPDATE deduction SET type = 'TAX' WHERE id = 'IRSA';
