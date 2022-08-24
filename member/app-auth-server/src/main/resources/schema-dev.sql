ALTER TABLE IF EXISTS member_interest DROP FOREIGN KEY member_id;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS member_interest;
DROP TABLE IF EXISTS member_oauth;
DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;

create table member (
    member_id bigint not null,
    created_date datetime(6) not null,
    modified_date datetime(6) not null,
    auth_id varchar(255),
    email varchar(255),
    link varchar(255),
    member_name varchar(255),
    nickname varchar(255),
    role varchar(255),
    primary key (member_id)
) engine=InnoDB;

create table member_interest (
    member_interest_id bigint not null,
    created_date datetime(6) not null,
    modified_date datetime(6) not null,
    category varchar(255),
    member_id bigint,
    primary key (member_interest_id)
) engine=INNODB;

create table member_oauth (
       id bigint not null,
       created_date datetime(6) not null,
       modified_date datetime(6) not null,
       access_token varchar(255),
       auth_id varchar(255),
       email varchar(255),
       expiration bigint,
       provider varchar(255),
       refresh_token varchar(255),
       primary key (id)
) engine=INNODB;

alter table member_interest add foreign key (member_id) references member (member_id);