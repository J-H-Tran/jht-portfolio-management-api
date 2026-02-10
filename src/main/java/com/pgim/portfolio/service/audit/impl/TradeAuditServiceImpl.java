package com.pgim.portfolio.service.audit.impl;

import com.pgim.portfolio.domain.TradeAuditMapper;
import com.pgim.portfolio.domain.dto.audit.TradeAuditDTO;
import com.pgim.portfolio.domain.entity.audit.TradeAudit;
import com.pgim.portfolio.repository.audit.AuditRepository;
import com.pgim.portfolio.repository.pm.TradeRepository;
import com.pgim.portfolio.service.audit.TradeAuditService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeAuditServiceImpl implements TradeAuditService {
    private final AuditRepository auditRepository;
    private final TradeRepository tradeRepository;
    private final TradeAuditMapper tradeAuditMapper;

    public TradeAuditServiceImpl(
            AuditRepository auditRepository,
            TradeRepository tradeRepository,
            TradeAuditMapper tradeAuditMapper
    ) {
        this.auditRepository = auditRepository;
        this.tradeRepository = tradeRepository;
        this.tradeAuditMapper = tradeAuditMapper;
    }

    /**
     * Logs an audit event for a trade.
     * Ensures the trade exists in the main DB before logging.
     */
    @Override
    @Transactional("auditTransactionManager")
    public void logTradeEvent(Long tradeId, String action, String details) {
        // Enforce referential integrity at the application layer
        var tradeOpt = tradeRepository.findById(tradeId);
        if (tradeOpt.isEmpty()) {
            throw new IllegalArgumentException("Trade not found for audit logging: " + tradeId);
        }
        TradeAudit audit = new TradeAudit();
        audit.setTradeId(tradeId);
        audit.setAction(TradeAudit.AuditAction.valueOf(action));
        audit.setDetails(details);
        auditRepository.save(audit);
    }

    /**
     * Retrieves all audit logs for a trade.
     */
    @Override
    public List<TradeAuditDTO> getAuditLogsForTrade(Long tradeId) {
        return auditRepository.findAll().stream()
                .filter(audit -> audit.getTradeId().equals(tradeId))
                .map(tradeAuditMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single audit log by its ID.
     */
    @Override
    public TradeAuditDTO getAuditLogById(Long auditId) {
        return auditRepository.findById(auditId)
                .map(tradeAuditMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Audit log not found for audit id: " + auditId));
    }
}