ALTER TABLE invoice
    DROP COLUMN invoice_reference;

ALTER TABLE invoice
    ADD autoincrement INTEGER NOT NULL;

CREATE OR REPLACE FUNCTION set_autoincrement()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.autoincrement IS NULL THEN
        SELECT COALESCE(MAX(autoincrement), 0) + 1
        INTO NEW.autoincrement
        FROM invoice
        WHERE worker_code = NEW.worker_code;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_autoincrement
BEFORE INSERT ON invoice
FOR EACH ROW
EXECUTE FUNCTION set_autoincrement();