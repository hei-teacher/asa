-- taux horaire de base = salaire de base / 173,33 (base légale mensuelle en heures)
-- chaque type d'heure supp./majorée applique un taux de majoration différent sur ce taux horaire
INSERT INTO credit (id, "name", divisor, rate, number_of_units) VALUES
    ('HEURES_SUPP_NON_IMPOSABLE_130', 'Heures Supp. non Imposable 130%', 173.33, 130, NULL),
    ('HEURES_SUPP_NON_IMPOSABLE_150', 'Heures Supp. non imposable 150%', 173.33, 150, NULL),
    ('HEURES_SUPP_IMPOSABLE_150', 'Heures Supp. imposable 150%', 173.33, 150, NULL),
    ('HEURES_MAJOREES_NUIT_HABITUEL', 'Heures majorées trav nuit habituel', 173.33, 30, NULL),
    ('HEURES_MAJOREES_NUIT_OCCAS', 'Heures majorées trav nuit occas.', 173.33, 50, NULL),
    ('HEURES_MAJOREES_DIMANCHE', 'Heures majorées trav dimanche', 173.33, 40, NULL),
    ('HEURES_MAJOREES_JOURS_FERIES', 'Heures majorées trav jours fériés', 173.33, 50, NULL);
