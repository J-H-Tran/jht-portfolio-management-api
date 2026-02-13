-- Initial Data for user_db (AppUser and AppUserRole entities)

-- Insert sample users (password: 'password123' BCrypt hashed)
-- BCrypt hash for 'password123': $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy
INSERT INTO users (username, email, password, first_name, last_name, enabled) VALUES
    ('admin', 'admin@user.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy', 'Admin', 'User', TRUE),
    ('jdoe', 'jdoe@user.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy', 'John', 'Doe', TRUE),
    ('manager', 'manager@user.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYCxO8GBvmy', 'Portfolio', 'Manager', TRUE);

-- Assign roles to users
-- Note: role_id values should correspond to the `auth_roles` table in portfolio_db
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 2), -- admin -> ADMIN
    (1, 3), -- admin -> MANAGER
    (2, 1), -- jdoe -> USER
    (3, 3); -- manager -> MANAGER