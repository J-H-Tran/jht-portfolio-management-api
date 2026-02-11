package com.pgim.portfolio.domain.dto.audit;

import java.time.LocalDateTime;

public record TradeAuditDTO (
    Long id,
    Long tradeId,
    String action,
    String details,
    LocalDateTime createdAt
) {}