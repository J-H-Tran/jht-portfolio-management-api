-- Audit Database Schema (Secondary Datasource)
-- 3NF Compliant Schema for Trade Audit Logs

-- Drop existing table (for clean recreation)
DROP TABLE IF EXISTS trade_audit;

-- Trade Audit table (immutable audit logs)
CREATE TABLE trade_audit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trade_id BIGINT NOT NULL COMMENT 'References trades.id in primary database',
    action ENUM('CREATE', 'ADJUST', 'CANCEL') NOT NULL,
    details JSON NOT NULL COMMENT 'Audit details in JSON format',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trade_id (trade_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Immutable audit log for trade operations';