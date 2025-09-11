CREATE DATABASE springboot_rest_api;

USE springboot_rest_api;

CREATE TABLE users(
  username         VARCHAR(100) NOT NULL,
  password         VARCHAR(100) NOT NULL,
  name             VARCHAR(100) NOT NULL,
  token            VARCHAR(100) NULL,
  token_expired_at BIGINT,
  PRIMARY KEY (username),
  UNIQUE(token)
) ENGINE InnoDB;

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

-- Insert Users
INSERT INTO users (username, password, name, token, token_expired_at) VALUES
('john_doe', 'hashedpassword123', 'John Doe', 'token123', 1700000000),
('jane_smith', 'hashedpassword456', 'Jane Smith', 'token456', 1700005000),
('alex_k', 'hashedpassword789', 'Alex Kim', NULL, NULL);

-- Insert Contacts (linked to users)
INSERT INTO contacts (id, username, first_name, last_name, phone, email) VALUES
('c1', 'john_doe', 'Michael', 'Jordan', '08123456789', 'mjordan@example.com'),
('c2', 'john_doe', 'Sarah', 'Connor', '08987654321', 'sconnor@example.com'),
('c3', 'jane_smith', 'Bruce', 'Wayne', '0811223344', 'bwayne@example.com'),
('c4', 'alex_k', 'Clark', 'Kent', '0822334455', 'ckent@example.com');

-- Insert Addresses (linked to contacts)
INSERT INTO addresses (id, contact_id, street, city, province, country, postal_code) VALUES
('a1', 'c1', '123 Basketball St', 'Chicago', 'Illinois', 'USA', '60601'),
('a2', 'c2', '45 Future Rd', 'Los Angeles', 'California', 'USA', '90001'),
('a3', 'c3', '100 Wayne Tower', 'Gotham', 'New Jersey', 'USA', '07001'),
('a4', 'c4', '200 Daily Planet', 'Metropolis', 'New York', 'USA', '10001');