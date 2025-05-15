DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NULL,
    modified_at datetime              NULL,
    email       VARCHAR(255)          NULL,
    password    VARCHAR(255)          NULL,
    nickname    VARCHAR(255)          NULL,
    user_role   VARCHAR(255)          NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

CREATE INDEX idx_nickname ON users (nickname);

LOAD DATA LOCAL INFILE '/docker-entrypoint-initdb.d/users.csv'
    INTO TABLE users
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (created_at, modified_at, email, password, nickname, user_role);

# testcontainers로 테스트 시 주석 해제
# LOAD DATA LOCAL INFILE 'src/test/resources/db/init/users.csv'
#     INTO TABLE users
#     FIELDS TERMINATED BY ','
#     ENCLOSED BY '"'
#     LINES TERMINATED BY '\n'
#     IGNORE 1 LINES
#     (created_at, modified_at, email, password, nickname, user_role);
