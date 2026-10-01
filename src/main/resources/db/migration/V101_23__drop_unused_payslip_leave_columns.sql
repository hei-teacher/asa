ALTER TABLE payslip
    DROP COLUMN IF EXISTS leave_base_amount,
    DROP COLUMN IF EXISTS leave_amount;
