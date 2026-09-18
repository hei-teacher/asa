INSERT INTO contract_level
 (code, type, daily_pay)
VALUES ( 'L5P-2026', 'partnerContractor', 50000),
       ( 'L4P-2026', 'partnerContractor', 100000);

UPDATE contract_level SET type = 'studentContractor', daily_pay = 50000 WHERE code = 'L5';
