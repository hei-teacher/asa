insert into worker (code, name, email, fullname, address, city, nif, stat)
values ('worker-code', 'code', 'email', 'full code', 'address', 'random city', 'nif', 'stat');

INSERT INTO contract
    (id, worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, company, contract_bucket_key)
VALUES ('daily-execution-it-contract',
        'worker-code',
        'L4P-2026',
        '2019-01-01 00:00:00.000000',
        '2019-12-31 00:00:00.000000',
        'Test Job',
        365,
        'Test Company',
        'test-key');
