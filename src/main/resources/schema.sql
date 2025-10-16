-- テーブルが既に存在する場合は削除 (開発・テスト用)
DROP TABLE IF EXISTS role_table;
DROP TABLE IF EXISTS user_table;

-- ユーザーテーブルの作成
CREATE TABLE user_table (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ロールテーブルの作成
CREATE TABLE role_table (
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, name),
    FOREIGN KEY (user_id) REFERENCES user_table(id)
);
