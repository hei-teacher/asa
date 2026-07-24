insert into worker (code, name, email, fullname, address, city, nif, stat)
values ('worker-code', 'code', 'email', 'full code', 'address', 'random city', 'nif', 'stat');

-- entrance year >= 2026: excluded from findByYearBetween(2024, 2026)
INSERT INTO contract
    (id, worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, company, contract_bucket_key)
VALUES ('worker-code-it-active-contract',
        'worker-code',
        'L4P-2026',
        '2026-01-01 00:00:00.000000',
        NULL,
        'Test Job',
        365,
        'Test Company',
        'test-key');
