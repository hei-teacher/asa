-- La table payslip (V101_3) n'avait aucun lien vers worker : impossible de retrouver la fiche
-- de paie d'un salarie pour un mois donne. On l'aligne sur earned_credits/invoice.
ALTER TABLE payslip ADD COLUMN worker_code VARCHAR NOT NULL REFERENCES worker(code);
CREATE INDEX idx_payslip_worker_code ON payslip(worker_code);
ALTER TABLE payslip ADD CONSTRAINT uq_payslip_worker_year_month UNIQUE (worker_code, year_month);

-- paid_leave_amount/taken_paid_leave etaient en INT alors que les jours de conge s'accumulent
-- par fractions (ex: 2.5 jours/mois, cf. V101_14) : INT tronquait la valeur reelle.
ALTER TABLE payslip ALTER COLUMN paid_leave_amount TYPE NUMERIC(5,2);
ALTER TABLE payslip ALTER COLUMN taken_paid_leave TYPE NUMERIC(5,2);
