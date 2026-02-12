-- Initial Data for Portfolio Management Database
-- Insert default roles and sample data

-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('USER', 'Standard user with basic access'),
    ('ADMIN', 'Administrator with full access'),
    ('MANAGER', 'Portfolio manager with advanced access');

-- Insert sample users (password: 'password123' BCrypt hashed)
-- BCrypt hash for 'password123': $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy
INSERT INTO users (username, email, password, first_name, last_name, enabled) VALUES
    ('admin', 'admin@portfolio.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy', 'Admin', 'User', TRUE),
    ('jdoe', 'jdoe@portfolio.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy', 'John', 'Doe', TRUE),
    ('manager', 'manager@portfolio.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy', 'Portfolio', 'Manager', TRUE);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 2), -- admin -> ADMIN
    (1, 3), -- admin -> MANAGER
    (2, 1), -- jdoe -> USER
    (3, 3); -- manager -> MANAGER

-- Insert sample portfolios
INSERT INTO portfolios (name) VALUES
    ('Growth Portfolio'),
    ('Income Portfolio'),
    ('Balanced Portfolio');

-- Insert sample trades
INSERT INTO trades (portfolio_id, trade_reference_id, trade_type, quantity, price, status) VALUES
    (1, 'TREF1001', 'BUY', 100.0000, 50.2500, 'COMPLETED'),
    (1, 'TREF1002', 'SELL', 50.0000, 51.0000, 'COMPLETED'),
    (2, 'TREF2001', 'BUY', 200.0000, 75.5000, 'PENDING'),
    (3, 'TREF3001', 'BUY', 150.0000, 100.0000, 'VALIDATED');