CREATE TABLE IF NOT EXISTS payslip (
  id VARCHAR PRIMARY KEY,
  year_month VARCHAR NOT NULL,
  gross_amount NUMERIC(12,2),
  net_amount NUMERIC(12,2),
  total_amount NUMERIC(12,2),
  paid_leave_amount INT,
  taken_paid_leave INT,
  leave_base_amount NUMERIC(12,2),
  leave_amount NUMERIC(12,2)
);

CREATE TABLE IF NOT EXISTS payslip_tax (
  payslip_id VARCHAR NOT NULL REFERENCES payslip(id),
  tax_id VARCHAR NOT NULL REFERENCES tax(id),
  PRIMARY KEY (payslip_id, tax_id)
);

CREATE TABLE IF NOT EXISTS payslip_credit (
  payslip_id VARCHAR NOT NULL REFERENCES payslip(id),
  credit_id VARCHAR NOT NULL REFERENCES credit(id),
  PRIMARY KEY (payslip_id, credit_id)
);
