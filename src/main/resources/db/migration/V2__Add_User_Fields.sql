-- Add missing columns to users table
ALTER TABLE users ADD COLUMN email VARCHAR(255) NOT NULL UNIQUE AFTER password;
ALTER TABLE users ADD COLUMN first_name VARCHAR(255) AFTER email;
ALTER TABLE users ADD COLUMN last_name VARCHAR(255) AFTER first_name;
ALTER TABLE users ADD COLUMN enabled BOOLEAN DEFAULT TRUE AFTER last_name;

-- Ensure user_roles has the correct role column
-- If the table already has a 'roles' column, we may need to migrate data
-- This is handled by the @Column(name="role") annotation in the entity
