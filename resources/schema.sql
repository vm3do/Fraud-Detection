-- Table: Card
CREATE TABLE Card (
    id INT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50) UNIQUE NOT NULL,
    expirationDate DATE NOT NULL,
    status ENUM('ACTIVE', 'SUSPENDED', 'BLOCKED') NOT NULL,
    type ENUM('DEBIT', 'CREDIT', 'PREPAID') NOT NULL,
    clientId INT NOT NULL,
    dailyLimit DECIMAL(10,2),     -- only for debit
    monthlyLimit DECIMAL(10,2),   -- only for credit
    interestRate DECIMAL(5,2),    -- only for credit
    balance DECIMAL(10,2),        -- only for prepaid
    FOREIGN KEY (clientId) REFERENCES Client(id) ON DELETE CASCADE
);

-- Table: CardOperation
CREATE TABLE CardOperation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    operationDate DATETIME NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    operationType ENUM('PURCHASE', 'WITHDRAWAL', 'ONLINEPAYMENT') NOT NULL,
    location VARCHAR(100) NOT NULL,
    cardId INT NOT NULL,
    FOREIGN KEY (cardId) REFERENCES Card(id) ON DELETE CASCADE
);

-- Table: FraudAlert
CREATE TABLE FraudAlert (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    level ENUM('INFO', 'WARNING', 'CRITICAL') NOT NULL,
    cardId INT NOT NULL,
    FOREIGN KEY (cardId) REFERENCES Card(id) ON DELETE CASCADE
);
