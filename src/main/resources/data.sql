-- H2 DBはPostgreSQLのように自動シーケンスを生成しない場合があるため、明示的にIDを指定
INSERT INTO user_master (id, name, email, password, is_active, created_at, updated_at)
VALUES
(
    1,
    'Admin User',
    'admin@example.com',
    '$2a$10$u/PayOaUO96YklC0trr9.ezsEw7pKNUmYQ1ZGI5zhf2NY1A/SMnd6', 
    TRUE,
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
)
,(
    2,
    'Regular User',
    'user@example.com',
    '$2a$10$D02/ZV6Wb7Bw1BjQ.sNumugMY3VYpPdN/aJOoiNow3FX0V1WO.Cmq',
    TRUE,
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
);

-- ロールの投入
INSERT INTO role_definition (id, name)
VALUES 
 (1, 'ADMIN')
,(2, 'USER')
;

-- ユーザーロールの投入
INSERT INTO user_role (user_id, role_id)
VALUES
 (1, 1) -- Admin UserにADMINロールを付与
,(1, 2) -- Admin UserにUSERロールを付与
,(2, 2) -- Regular UserにUSERロールを付与
;