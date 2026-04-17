
create database GadStore;
CREATE TABLE useraccounts (
                              Id INT PRIMARY KEY AUTO_INCREMENT,
                              Name VARCHAR(100) NOT NULL,
                              Password VARCHAR(255) NOT NULL
);

CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          quantity INT NOT NULL,
                          price DOUBLE NOT NULL
);