package com.pgim.portfolio.service.audit;

import com.pgim.portfolio.domain.dto.audit.TradeAuditDTO;
import com.pgim.portfolio.domain.entity.audit.TradeAudit.AuditAction;

import java.util.List;

public interface TradeAuditService {
    void logTradeEvent(Long tradeId, AuditAction action, String details);
    List<TradeAuditDTO> getAuditLogsForTrade(Long tradeId);
    TradeAuditDTO getAuditLogById(Long auditId);
}