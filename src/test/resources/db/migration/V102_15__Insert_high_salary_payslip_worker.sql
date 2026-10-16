insert into worker (code, name)
values ('W-PAYSLIP-02', 'Hery Randria');

INSERT INTO contract_level (code, type, monthly_pay, paid_leave_days_number)
VALUES ('FTE-PAYSLIP-HIGH', 'fullTimeEmployee', 1412612.26, 2.5);

INSERT INTO contract
    (id, worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-payslip-02', 'W-PAYSLIP-02', 'FTE-PAYSLIP-HIGH', '2024-01-01 08:00:00.000000',
        'Ingenieur', 0, 'contract_bucket_key');
