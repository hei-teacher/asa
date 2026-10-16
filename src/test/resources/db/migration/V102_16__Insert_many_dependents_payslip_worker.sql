insert into worker (code, name, kids_number)
values ('W-PAYSLIP-03', 'Zo Rakotoarisoa', 8);

INSERT INTO contract_level (code, type, monthly_pay, paid_leave_days_number)
VALUES ('FTE-PAYSLIP-DEPENDENTS', 'fullTimeEmployee', 530000, 2.5);

INSERT INTO contract
    (id, worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-payslip-03', 'W-PAYSLIP-03', 'FTE-PAYSLIP-DEPENDENTS', '2024-01-01 08:00:00.000000',
        'Technicien', 0, 'contract_bucket_key');
