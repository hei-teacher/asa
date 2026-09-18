ALTER TABLE payslip ADD COLUMN worker_code VARCHAR NOT NULL REFERENCES worker(code);
CREATE INDEX idx_payslip_worker_code ON payslip(worker_code);
ALTER TABLE payslip ADD CONSTRAINT uq_payslip_worker_year_month UNIQUE (worker_code, year_month);

ALTER TABLE payslip ALTER COLUMN paid_leave_amount TYPE NUMERIC(5,2);
ALTER TABLE payslip ALTER COLUMN taken_paid_leave TYPE NUMERIC(5,2);
