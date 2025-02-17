insert into mission_execution (id, date, mission_code, worker_code, day_percentage, comment)
VALUES ('me_id_0', '2024-01-01', 'mission0-code', 'W-P-2024-01', '0.8', 'comment0'),
       --duplicate of me_id_1 to test out duplicates
       ('me_id_1', '2024-01-01', 'mission0-code', 'W-P-2024-01', '0.8', 'comment0'),
       ('me_id_2', '2024-01-01', 'mission0-code', 'W-P-2024-01', '0.2', 'comment1');