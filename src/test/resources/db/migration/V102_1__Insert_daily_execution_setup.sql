INSERT INTO worker (code, name, email, fullname, address, city, nif, stat)
VALUES ('worker-code', 'code', 'email', 'full code', 'address', 'random city', 'nif', 'stat')
ON CONFLICT DO NOTHING;

INSERT INTO contract_level (code, type, daily_pay)
VALUES ('L-TEST', 'studentContractor', 25000)
ON CONFLICT DO NOTHING;

INSERT INTO contract (id, worker_code, level, entrance_instant, duration_in_days)
VALUES ('contract-daily-exec', 'worker-code', 'L-TEST', '2025-01-01 00:00:00'::timestamp, 80)
ON CONFLICT DO NOTHING;
