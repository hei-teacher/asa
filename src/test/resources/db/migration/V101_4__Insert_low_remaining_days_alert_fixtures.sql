insert into worker (code, name)
values ('alert-worker-below', 'Alert Worker Below'),
       ('alert-worker-above', 'Alert Worker Above'),
       ('alert-worker-none', 'Alert Worker None');

INSERT INTO contract
    (id, worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('alert-contract-below', 'alert-worker-below', 'L4P-2026', '2027-07-01 00:00:00.000000', 'Test Job', 5, 'test-key'),
       ('alert-contract-above', 'alert-worker-above', 'L4P-2026', '2027-08-01 00:00:00.000000', 'Test Job', 15, 'test-key');
