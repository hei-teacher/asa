UPDATE deduction SET type = 'DEDUCTION' WHERE id IN ('CNAPS', 'OSTIE', 'FMFP');
UPDATE deduction SET type = 'TAX' WHERE id = 'IRSA';
