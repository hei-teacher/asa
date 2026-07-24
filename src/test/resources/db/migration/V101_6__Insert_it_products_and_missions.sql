INSERT INTO product (code, name, description)
VALUES ('pcode', 'pname', 'pdescription'),
       ('dummy-care-product-code', '', '');

INSERT INTO mission (code, title, description, max_duration_in_days, product_code)
VALUES ('mission1-code', 'title1', 'description1', 10, 'pcode'),
       ('mission2-code', 'title2', 'description2', 2, 'pcode'),
       ('careMission-code', '', '', 2, 'dummy-care-product-code');
