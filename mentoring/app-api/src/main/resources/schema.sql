CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS program (
  program_id bigint NOT NULL AUTO_INCREMENT,
  created_date datetime(6) NOT NULL,
  modified_date datetime(6) NOT NULL,
  category varchar(255),
  cur_num integer NOT NULL,
  description varchar(255),
  due_date date,
  goal varchar(255),
  is_open bit NOT NULL,
  max_member integer,
  title varchar(255),
  PRIMARY KEY (program_id)
) engine=InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS participant (
      participant_id bigint NOT NULL AUTO_INCREMENT,
      created_date datetime(6) NOT NULL,
      modified_date datetime(6) NOT NULL,
      approval bit not null,
      is_host bit not null,
      member_id bigint,
      message varchar(255),
      nickname varchar(255),
      role varchar(255),
      program_id bigint,
      PRIMARY KEY (participant_id),
      FOREIGN KEY (program_id) REFERENCES program (program_id)
) engine=InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mission (
      mission_id bigint NOT NULL AUTO_INCREMENT,
      created_date datetime(6) NOT NULL,
      modified_date datetime(6) NOT NULL,
      content varchar(255),
      due_date date,
      goal varchar(255),
      title varchar(255),
      program_id bigint,
      PRIMARY KEY (mission_id),
      FOREIGN KEY (program_id) REFERENCES program (program_id)
) engine=InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS board (
      board_id bigint NOT NULL AUTO_INCREMENT,
      created_date datetime(6) NOT NULL,
      modified_date datetime(6) NOT NULL,
      content varchar(255),
      member_id bigint,
      nickname varchar(255),
      mission_id bigint,
      PRIMARY KEY (board_id),
      FOREIGN KEY (mission_id) REFERENCES mission (mission_id)
)engine=InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;

