UPDATE contract_level
SET paid_leave_days_number = 2.5
WHERE type = 'fullTimeEmployee' AND paid_leave_days_number IS NULL;
