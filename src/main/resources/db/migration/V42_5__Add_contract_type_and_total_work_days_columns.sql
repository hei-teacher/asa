DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_type WHERE typname = 'contract_type') THEN
        CREATE TYPE contract_type AS ENUM ('studentContractor', 'partnerContractor', 'fullTimeEmployee');
    END IF;
END
$$;

ALTER TABLE worker_level_history
    ADD COLUMN IF NOT EXISTS contract_type contract_type;

ALTER TABLE worker_level_history
    ADD COLUMN IF NOT EXISTS total_work_days INT;
