package com.pgim.portfolio.domain.dto.audit;

import com.pgim.portfolio.domain.entity.audit.AuditDetails;
import java.time.LocalDateTime;

public record TradeAuditDTO (
    Long id,
    Long tradeId,
    String action,
    AuditDetails details,
    LocalDateTime createdAt
) {}