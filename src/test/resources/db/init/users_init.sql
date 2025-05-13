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