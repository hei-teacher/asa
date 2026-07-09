insert into worker (code, name)
values ('worker-alert', 'Alert Worker');

INSERT INTO contract_level
 (code, type, daily_pay)
VALUES ('L-TEST-ALERT', 'studentContractor', 25000);

INSERT INTO contract
    (id,worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-alert','worker-alert','L-TEST-ALERT','2025-01-01 00:00:00.000000','job_title', 10, 'contract_bucket_key');

insert into mission_execution (id, mission_code, worker_code, date, day_percentage, creation_instant, comment)
VALUES ('me-used-day', 'mission-alert-code', 'worker-alert', '2025-01-01', 1.0, '2025-01-01 12:00:00.000000', 'first day');
