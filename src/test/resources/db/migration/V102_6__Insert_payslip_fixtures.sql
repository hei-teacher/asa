-- fixture pour les tests d'integration de generatePaySlip (fullTimeEmployee)
insert into worker (code, name)
values ('W-PAYSLIP-01', 'Rina Rakoto');

INSERT INTO contract_level (code, type, monthly_pay, paid_leave_days_number)
VALUES ('FTE-PAYSLIP', 'fullTimeEmployee', 450000, 2.5);

INSERT INTO contract
    (id, worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-payslip-01', 'W-PAYSLIP-01', 'FTE-PAYSLIP', '2024-01-01 08:00:00.000000',
        'Comptable', 0, 'contract_bucket_key');

-- 2 jours d'absence sur avril 2026, pour verifier le chemin "credit gagne" (ABSENCE, taxable)
INSERT INTO earned_credits (id, credit_code, number_of_units, year_month, worker_code)
VALUES ('earned-credit-payslip-01', 'ABSENCE', 2, '2026-04', 'W-PAYSLIP-01');
