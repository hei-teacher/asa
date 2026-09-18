-- 'worker-code' a deja un contrat actif dedie (V102_5, pour les tests de pointage) : celui-ci est
-- termine des l'insertion pour ne pas violer la regle "un seul contrat actif par worker" et faire
-- planter findActiveContractByWorker (2 resultats au lieu d'un).
INSERT INTO contract
(id,worker_code, level, entrance_instant, end_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('new-id','worker-code','L4P-2026','2025-01-01 08:00:00.000000','2025-06-01 08:00:00.000000','job_title', 80, 'contract_bucket_key');