-- テーブルが既に存在する場合は削除 (開発・テスト用)
DROP TABLE IF EXISTS batch_execution_history;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role_definition;
DROP TABLE IF EXISTS user_master;

-- ユーザーテーブルの作成
CREATE TABLE user_master (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ロールテーブルの作成
CREATE TABLE role_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- ユーザーロール中間テーブルの作成 (将来的に多対多の関係をサポートする場合に備えて)
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_master(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role_definition(id)
);

-- バッチ実行履歴テーブルの作成
CREATE TABLE batch_execution_history (
    id VARCHAR(36) PRIMARY KEY,
    job_id VARCHAR(100) NOT NULL,
    job_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    exit_code INT,
    user_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_master(id) ON DELETE CASCADE
);

CREATE INDEX idx_batch_job_id ON batch_execution_history(job_id);
CREATE INDEX idx_batch_user_id ON batch_execution_history(user_id);
CREATE INDEX idx_batch_start_time ON batch_execution_history(start_time);
