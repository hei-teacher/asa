INSERT INTO credit (id, credit_code, "name", divisor, rate) VALUES
    ('TEST_NEGATIVE_RETENUE', 'TEST_NEGATIVE_RETENUE', 'Retenue de test (negative)', 1, -100);

INSERT INTO earned_credits (id, credit_code, number_of_units, year_month, worker_code)
VALUES ('earned-credit-payslip-negative', 'TEST_NEGATIVE_RETENUE', 2, '2026-05', 'W-PAYSLIP-01');
