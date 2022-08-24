SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS member_interest;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS member_oauth;
DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE member (
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
) engine=InnoDB;

CREATE TABLE member_interest (
    member_interest_id bigint NOT NULL AUTO_INCREMENT,
    created_date datetime(6) NOT NULL,
    modified_date datetime(6) NOT NULL,
    category varchar(255),
    member_id bigint,
    PRIMARY KEY (member_interest_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
) engine=INNODB;

CREATE TABLE member_oauth (
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
) engine=INNODB;

SET FOREIGN_KEY_CHECKS = 1;