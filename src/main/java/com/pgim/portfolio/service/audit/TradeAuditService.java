package com.pgim.portfolio.service.audit;

import com.pgim.portfolio.domain.dto.audit.TradeAuditDTO;
import java.util.List;

public interface TradeAuditService {
    void logTradeEvent(Long tradeId, String action, String details);
    List<TradeAuditDTO> getAuditLogsForTrade(Long tradeId);
    TradeAuditDTO getAuditLogById(Long auditId);

}