-- H2 DBはPostgreSQLのように自動シーケンスを生成しない場合があるため、明示的にIDを指定
INSERT INTO user_table (id, name, email, password, is_active, created_at, updated_at)
VALUES (
    1,
    'Admin User',
    'admin@example.com',
    '$2a$10$u/PayOaUO96YklC0trr9.ezsEw7pKNUmYQ1ZGI5zhf2NY1A/SMnd6', 
    TRUE,
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
);

-- ロールの投入
INSERT INTO role_table (user_id, role) VALUES (1, 'ADMIN');
INSERT INTO role_table (user_id, role) VALUES (1, 'USER');
