insert into worker (code, name)
values ('W-101', 'John');

INSERT INTO contract
    (id,worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('1234','W-101','L4P-2026','2024-01-01 08:00:00.000000','2024-06-01 08:00:00.000000','job_title', 80, 'contract_bucket_key');
