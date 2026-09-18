-- 2,5 jours/mois = minimum legal standard (30 jours ouvrables/an) pour un salarie permanent.
-- Ne s'applique qu'aux fullTimeEmployee : un DEFAULT de colonne ne peut pas etre conditionne
-- par "type", donc on backfill uniquement les lignes concernees plutot que d'en poser un global.
UPDATE contract_level
SET paid_leave_days_number = 2.5
WHERE type = 'fullTimeEmployee' AND paid_leave_days_number IS NULL;
