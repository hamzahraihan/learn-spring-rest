CREATE DATABASE springboot_rest_api;

USE springboot_rest_api

CREATE TABLE users(
  username         VARCHAR(100) NOT NULL,
  password         VARCHAR(100) NOT NULL,
  name             VARCHAR(100) NOT NULL,
  token            VARCHAR(100) NULL,
  token_expired_at BIGINT,
  PRIMARY KEY (username),
  UNIQUE(token)
) ENGINE InnoDB;

SELECT * FROM users;

DESC users;

CREATE TABLE contacts(
  id          VARCHAR(100) NOT NULL,
  username    VARCHAR(100) NOT NULL,
  first_name  VARCHAR(100) NOT NULL,
  last_name   VARCHAR(100) NOT NULL,
  phone       VARCHAR(100) NULL,
  email       VARCHAR(100) NULL,
  PRIMARY KEY(id),
  FOREIGN KEY fk_users_contacts (username) REFERENCES users (username)
) ENGINE InnoDB;

SELECT * from contacts;

DESC contacts;

CREATE TABLE addresses(
  id          VARCHAR(100) NOT NULL,
  contact_id  VARCHAR(100) NOT NULL,
  street      VARCHAR(100),
  city        VARCHAR(100),
  province    VARCHAR(100),
  country     VARCHAR(100) NOT NULL,
  postal_code VARCHAR(10),
  PRIMARY KEY(id),
  FOREIGN KEY fk_contacts_addresses (contact_id) REFERENCES contacts(id)
) ENGINE InnoDB;

SELECT * FROM addresses;

DESC addresses;