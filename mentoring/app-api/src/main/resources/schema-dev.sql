SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS mission;
DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS program;
DROP SEQUENCE IF EXISTS hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;

create table board (
  board_id bigint not null,
  created_date datetime(6) not null,
  modified_date datetime(6) not null,
  content varchar(255),
  member_id bigint,
  nickname varchar(255),
  mission_id bigint,
  primary key (board_id)
) engine=INNODB;

create table mission (
  mission_id bigint not null,
  created_date datetime(6) not null,
  modified_date datetime(6) not null,
  content varchar(255),
  due_date date,
  goal varchar(255),
  title varchar(255),
  program_id bigint,
  primary key (mission_id)
) engine=INNODB;

create table participant (
  participant_id bigint not null,
  created_date datetime(6) not null,
  modified_date datetime(6) not null,
  approval bit not null,
  is_host bit not null,
  member_id bigint,
  message varchar(255),
  nickname varchar(255),
  role varchar(255),
  program_id bigint,
  primary key (participant_id)
) engine=INNODB;

create table program (
  program_id bigint not null,
  created_date datetime(6) not null,
  modified_date datetime(6) not null,
  category varchar(255),
  cur_num integer not null,
  description varchar(255),
  due_date date,
  goal varchar(255),
  is_open bit not null,
  max_member integer,
  title varchar(255),
  primary key (program_id)
) engine=INNODB;

alter table board add foreign key (mission_id) references mission (mission_id);
alter table mission add foreign key (program_id) references program (program_id);
alter table participant add foreign key (program_id) references program (program_id);

SET FOREIGN_KEY_CHECKS = 1;