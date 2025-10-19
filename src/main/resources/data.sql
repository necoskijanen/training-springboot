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
 ('EXECUTE_BATCH')
,('MANAGE_MASTER')
;


-- バッチ実行履歴の投入（adminユーザーで2件の成功データ）
INSERT INTO batch_execution_history (id, job_id, job_name, status, exit_code, user_id, start_time, end_time, created_at)
VALUES
  ('a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6', 'job-test1', 'test1', 'COMPLETED_SUCCESS', 0, 1, '2025-10-19 10:00:00', '2025-10-19 10:15:30', CURRENT_TIMESTAMP)
, ('b2c3d4e5-f6a7-48b9-c0d1-e2f3a4b5c6d7', 'job-test2', 'test2', 'COMPLETED_SUCCESS', 0, 1, '2025-10-19 08:30:00', '2025-10-19 08:45:15', CURRENT_TIMESTAMP)
;
