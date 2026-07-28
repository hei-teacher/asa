INSERT INTO contract_level (code, type, daily_pay)
VALUES ('L-CALENDAR', 'partnerContractor', 100000);

INSERT INTO contract
    (id,worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-calendar','worker-code','L-CALENDAR','2024-01-01 00:00:00.000000','job_title', 365, 'contract_bucket_key');