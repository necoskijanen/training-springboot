INSERT INTO user_master (name, email, password, is_active, admin, created_at, updated_at)
VALUES
(
    'admin',
    'admin@example.com',
    '$2a$10$u/PayOaUO96YklC0trr9.ezsEw7pKNUmYQ1ZGI5zhf2NY1A/SMnd6', 
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
)
,(
    'user',
    'user@example.com',
    '$2a$10$D02/ZV6Wb7Bw1BjQ.sNumugMY3VYpPdN/aJOoiNow3FX0V1WO.Cmq',
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
);

-- ロールの投入
INSERT INTO role_definition (name)
VALUES 
 ('ADMIN')
,('USER')
;

-- ユーザーロールの投入
INSERT INTO user_role (user_id, role_id)
VALUES
 (1, 1) -- Admin UserにADMINロールを付与
,(1, 2) -- Admin UserにUSERロールを付与
,(2, 2) -- Regular UserにUSERロールを付与
;