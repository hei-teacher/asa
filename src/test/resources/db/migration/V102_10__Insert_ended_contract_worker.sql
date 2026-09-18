-- fixture dediee pour tester un contrat termine : W-101 (V102_3) represente desormais un
-- contrat actif depuis le rebase, ce worker garde le scenario "contrat termine, aucun actif".
insert into worker (code, name)
values ('W-ENDED-01', 'Ended Worker');

INSERT INTO contract
    (id, worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('contract-ended-01', 'W-ENDED-01', 'L4P-2026', '2024-01-01 08:00:00.000000',
        '2024-06-01 08:00:00.000000', 'job_title', 80, 'contract_bucket_key');
