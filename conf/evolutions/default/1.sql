-- link schema

# --- !Ups
CREATE TABLE Link (
  id BIGINT NOT NULL auto_increment PRIMARY KEY,
  url VARCHAR(255) NOT NULL,
  shortUrl VARCHAR(255) NOT NULL
);

# --- !Downs
DROP TABLE Link