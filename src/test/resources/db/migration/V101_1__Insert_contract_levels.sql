INSERT INTO contract_level
 (level_id, code, type, daily_pay)
VALUES ('useless-id-to-rm', 'L5P', 'partnerContractor', 50000);

UPDATE contract_level SET type = 'studentContractor', daily_pay = 50000 WHERE code = 'L5';
