CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS member (
    member_id bigint NOT NULL AUTO_INCREMENT,
    created_date datetime(6) NOT NULL,
    modified_date datetime(6) NOT NULL,
    auth_id varchar(255),
    email varchar(255),
    link varchar(255),
    member_name varchar(255),
    nickname varchar(255),
    role varchar(255),
    primary key (member_id)
) engine=InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS member_interest (
    member_interest_id bigint NOT NULL AUTO_INCREMENT,
    created_date datetime(6) NOT NULL,
    modified_date datetime(6) NOT NULL,
    category varchar(255),
    member_id bigint,
    PRIMARY KEY (member_interest_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
) engine=INNODB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS member_oauth (
       id bigint NOT NULL AUTO_INCREMENT,
       created_date datetime(6) NOT NULL,
       modified_date datetime(6) NOT NULL,
       access_token varchar(255),
       auth_id varchar(255),
       email varchar(255),
       expiration bigint,
       provider varchar(255),
       refresh_token varchar(255),
       PRIMARY KEY (id)
) engine=INNODB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;
