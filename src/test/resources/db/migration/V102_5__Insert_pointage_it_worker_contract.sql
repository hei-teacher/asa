insert into worker (code, name)
values ('worker-code', 'code');

INSERT INTO contract
    (id, worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, contract_bucket_key, company)
VALUES (
    'it-worker-code-contract',
    'worker-code',
    'L4P-2026',
    '2010-01-01 00:00:00.000000',
    '2010-12-31 00:00:00.000000',
    'job-title',
    180,
    'contract-bucket-key',
    'company'
);
