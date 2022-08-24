CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS reply (
      reply_id bigint NOT NULL AUTO_INCREMENT,
      created_date datetime(6) NOT NULL,
      modified_date datetime(6) NOT NULL,
      board_id bigint,
      content varchar(255),
      member_id bigint,
      nickname varchar(255),
      PRIMARY KEY (reply_id)
) engine=INNODB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;