insert into worker (code, name)
values ('worker-no-days', 'No Days');

INSERT INTO contract_level
 (code, type, daily_pay)
VALUES ('L-TEST-EXHAUSTED', 'studentContractor', 25000);

INSERT INTO contract
    (id,worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-exhausted','worker-no-days','L-TEST-EXHAUSTED','2025-01-01 00:00:00.000000','job_title', 1, 'contract_bucket_key');

insert into mission_execution (id, mission_code, worker_code, date, day_percentage, creation_instant, comment)
VALUES ('me-exhausted', 'mission-ex-code', 'worker-no-days', '2025-01-01', 1.0, '2025-01-01 12:00:00.000000', 'already used day');
