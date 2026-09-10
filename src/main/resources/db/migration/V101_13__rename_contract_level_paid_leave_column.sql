-- "paidLeaveDays" (V100_9) a ete ajoutee avec une casse mixte entre guillemets, mais l'entite
-- JPA (@Column(name = "paidLeaveDaysNumber")) est physicalement mappee en snake_case par
-- Spring/Hibernate -> paid_leave_days_number. On renomme pour faire correspondre les deux.
ALTER TABLE contract_level RENAME COLUMN "paidLeaveDays" TO paid_leave_days_number;
