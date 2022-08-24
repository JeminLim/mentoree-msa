SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reply;
DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;

create table reply (
  reply_id bigint not null,
  created_date datetime(6) not null,
  modified_date datetime(6) not null,
  board_id bigint,
  content varchar(255),
  member_id bigint,
  nickname varchar(255),
  primary key (reply_id)
) engine=INNODB;

SET FOREIGN_KEY_CHECKS = 1;