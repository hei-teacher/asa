insert into worker (code, name)
values ('no-remaining-days-worker', 'No Remaining Days Worker');

insert into product (code, name, description)
values ('dummy-care-product-code', 'care', 'care');

insert into mission (code, title, description, max_duration_in_days, product_code)
values ('care-mission-code', 'care', 'care', 10, 'dummy-care-product-code');

INSERT INTO contract
    (id, worker_code, level, entrance_instant, job_title, duration_in_days, contract_bucket_key)
VALUES ('no-remaining-days-contract', 'no-remaining-days-worker', 'L4P-2026', '2024-01-01 00:00:00.000000',
        'job_title', 1, 'contract_bucket_key');

-- 0.5 day worked out of 1 : 0.5 day remaining
insert into mission_execution (id, date, mission_code, worker_code, day_percentage, comment)
VALUES ('no-remaining-days-me-1', '2024-01-02', 'mission0-code', 'no-remaining-days-worker', '0.5', 'comment'),
       ('no-remaining-days-me-2', '2024-01-02', 'care-mission-code', 'no-remaining-days-worker', '0.5', 'comment');
