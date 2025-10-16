-- テーブルが既に存在する場合は削除 (開発・テスト用)
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role_definition;
DROP TABLE IF EXISTS user_master;

-- ユーザーテーブルの作成
CREATE TABLE user_master (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ロールテーブルの作成
CREATE TABLE role_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- ユーザーロール中間テーブルの作成 (将来的に多対多の関係をサポートする場合に備えて)
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_master(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role_definition(id) ON DELETE CASCADE
);
