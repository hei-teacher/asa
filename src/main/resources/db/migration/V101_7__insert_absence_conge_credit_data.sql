-- base journaliere pour les lignes Absence / Allocation conge paye = salaire de base / 24 jours
INSERT INTO credit (id, "name", divisor, rate, number_of_units) VALUES
    ('ABSENCE', 'Absence', 24, 100, NULL),
    ('ALLOCATION_CONGE_PAYE', 'Allocation congé payé', 24, 100, NULL);
