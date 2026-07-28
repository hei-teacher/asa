INSERT INTO worker (code, name, email, full_name, address, city, nif, stat)
VALUES ('worker-code', 'code', 'email', 'full code', 'address', 'random city', 'nif', 'stat');

INSERT INTO contract_level (code, type, daily_pay)
VALUES ('L-TEST', 'studentContractor', 25000);

INSERT INTO contract
    (id,worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('test-contract-id','worker-code','L-TEST','2025-01-01 00:00:00.000000','job_title', 80, 'contract_bucket_key');