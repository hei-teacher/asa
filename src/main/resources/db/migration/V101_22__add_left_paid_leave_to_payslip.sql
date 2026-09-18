-- Le solde de conge final ("left" = notTakenTheLastMonth + base - taken, affiche sur la fiche
-- de paie) n'etait persiste nulle part : il etait uniquement recalcule dynamiquement a partir de
-- mission_execution, donc perdu/instable si ces donnees sont corrigees plus tard.
ALTER TABLE payslip ADD COLUMN left_paid_leave NUMERIC(5,2);
