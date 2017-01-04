CREATE TABLE IF NOT EXISTS product (
  id        INT(11)        NOT NULL AUTO_INCREMENT,
  name      VARCHAR(60)    NOT NULL,
  timestamp TIMESTAMP      NOT NULL,
  price     DECIMAL(14, 2) NOT NULL,
  version   INT            NOT NULL DEFAULT 0,
  UNIQUE (name, TIMESTAMP),
  PRIMARY KEY (id)
);

