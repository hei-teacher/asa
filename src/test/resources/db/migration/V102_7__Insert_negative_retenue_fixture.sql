-- fixture pour verifier le comportement quand une retenue diverse ecrase le salaire imposable
-- (credit.rate peut aller jusqu'a -100 depuis V101_7_1) : isolee de RETENUES_DIVERSES
-- (rate=0 globalement) pour ne pas affecter les autres tests.
INSERT INTO credit (id, credit_code, "name", divisor, rate) VALUES
    ('TEST_NEGATIVE_RETENUE', 'TEST_NEGATIVE_RETENUE', 'Retenue de test (negative)', 1, -100);

-- 2 unites : (450000/1) x -100% x 2 = -900 000, largement negatif
INSERT INTO earned_credits (id, credit_code, number_of_units, year_month, worker_code)
VALUES ('earned-credit-payslip-negative', 'TEST_NEGATIVE_RETENUE', 2, '2026-05', 'W-PAYSLIP-01');
