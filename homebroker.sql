CREATE DATABASE homebroker;

USE homebroker;

-- Tabela Users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Store hashed passwords
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela Accounts
CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00, -- User's account balance
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
);


-- Tabela para historico de operacoes
CREATE TABLE operations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    operation_type ENUM('BUY', 'SELL') NOT NULL,
    quantity INT NOT NULL,
    price_per_stock DECIMAL(10, 2) NOT NULL, -- Price at the time of operation
    total_value DECIMAL(15, 2) NOT NULL,     -- quantity * price_per_stock
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
);