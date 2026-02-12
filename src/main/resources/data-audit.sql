-- Initial Data for Audit Database
-- Insert sample audit logs for the trades created in the primary database

INSERT INTO trade_audit (trade_id, action, details) VALUES
    (1, 'CREATE', '{"user": "admin", "timestamp": "2026-02-10T10:00:00", "note": "Initial trade creation"}'),
    (2, 'CREATE', '{"user": "admin", "timestamp": "2026-02-10T11:00:00", "note": "Sell order created"}'),
    (3, 'CREATE', '{"user": "jdoe", "timestamp": "2026-02-10T12:00:00", "note": "Income portfolio trade"}'),
    (4, 'CREATE', '{"user": "manager", "timestamp": "2026-02-10T13:00:00", "note": "Balanced portfolio trade"}');