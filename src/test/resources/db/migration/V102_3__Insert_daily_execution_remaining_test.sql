insert into worker (code, name)
values ('worker-remaining', 'Remaining');

INSERT INTO contract_level
 (code, type, daily_pay)
VALUES ('L-TEST-REMAINING', 'studentContractor', 25000);

INSERT INTO contract
    (id,worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-remaining','worker-remaining','L-TEST-REMAINING','2025-01-01 00:00:00.000000','job_title', 80, 'contract_bucket_key');
