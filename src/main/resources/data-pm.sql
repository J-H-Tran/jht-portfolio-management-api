-- Initial Data for Portfolio Management Database
-- Insert default roles and sample data

-- Insert default roles
INSERT INTO auth_roles (name, description) VALUES
    ('USER', 'Standard user with basic access'),
    ('ADMIN', 'Administrator with full access'),
    ('MANAGER', 'Portfolio manager with advanced access');

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