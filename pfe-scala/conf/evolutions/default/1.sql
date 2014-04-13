# --- !Ups

CREATE TABLE items (
  id IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  price DOUBLE NOT NULL
);

# --- !Downs

DROP TABLE items;