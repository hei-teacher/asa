INSERT INTO contract_level (code, type, daily_pay)
VALUES ('L-CALENDAR', 'partnerContractor', 100000.0)
ON CONFLICT DO NOTHING;

INSERT INTO contract (id, worker_code, level, entrance_instant, duration_in_days)
VALUES ('contract-calendar', 'worker-code', 'L-CALENDAR', '2024-01-01 00:00:00', 365)
ON CONFLICT DO NOTHING;
