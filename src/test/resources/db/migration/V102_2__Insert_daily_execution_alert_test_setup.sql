INSERT INTO worker (code, name, email, fullname, address, city, nif, stat)
VALUES ('exhausted-test-worker', 'name', 'email', 'full', 'addr', 'city', 'nif', 'stat');

INSERT INTO contract
    (id,worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('exhausted-contract-id','exhausted-test-worker','L-TEST','2024-01-01 00:00:00.000000','2024-06-01 00:00:00.000000','job_title', 80, 'contract_bucket_key');

INSERT INTO worker (code, name, email, fullname, address, city, nif, stat)
VALUES ('alert-test-worker', 'name', 'email', 'full', 'addr', 'city', 'nif', 'stat');

INSERT INTO contract
    (id,worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('alert-contract-id','alert-test-worker','L-TEST','2024-01-01 00:00:00.000000','job_title', 5, 'contract_bucket_key');