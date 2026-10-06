insert into worker (code, name)
values ('auto-close-worker', 'Auto Close Worker'),
       ('auto-close-partial-worker', 'Auto Close Partial Worker'),
       ('auto-close-care-worker', 'Auto Close Care Worker');

INSERT INTO contract
    (id, worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('auto-close-contract', 'auto-close-worker', 'L4P-2026', '2024-01-01 00:00:00.000000',
        'job_title', 1, 'contract_bucket_key'),
       ('auto-close-partial-contract', 'auto-close-partial-worker', 'L4P-2026', '2024-01-01 00:00:00.000000',
        'job_title', 1, 'contract_bucket_key'),
       ('auto-close-care-contract', 'auto-close-care-worker', 'L4P-2026', '2024-01-01 00:00:00.000000',
        'job_title', 1, 'contract_bucket_key');
