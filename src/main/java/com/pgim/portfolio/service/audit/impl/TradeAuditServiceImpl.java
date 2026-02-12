package com.pgim.portfolio.service.audit.impl;

import com.pgim.portfolio.domain.TradeAuditMapper;
import com.pgim.portfolio.domain.dto.audit.TradeAuditDTO;
import com.pgim.portfolio.domain.entity.audit.AuditDetails;
import com.pgim.portfolio.domain.entity.audit.TradeAudit;
import com.pgim.portfolio.domain.entity.audit.TradeAudit.AuditAction;
import com.pgim.portfolio.repository.audit.AuditRepository;
import com.pgim.portfolio.repository.pm.TradeRepository;
import com.pgim.portfolio.service.audit.TradeAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pgim.portfolio.api.constant.CommonConstants.AUDIT_TRANSACTION_MANAGER;

@Service
public class TradeAuditServiceImpl implements TradeAuditService {
    private final TradeRepository tradeRepository;
    private final AuditRepository auditRepository;
    private final TradeAuditMapper tradeAuditMapper;

    public TradeAuditServiceImpl(
            TradeRepository tradeRepository,
            AuditRepository auditRepository,
            TradeAuditMapper tradeAuditMapper
    ) {
        this.tradeRepository = tradeRepository;
        this.auditRepository = auditRepository;
        this.tradeAuditMapper = tradeAuditMapper;
    }

    /**
     * Logs an audit event for a trade.
     * Ensures the trade exists in the main DB before logging.
     */
    @Override
    @Transactional(AUDIT_TRANSACTION_MANAGER)
    public void logTradeEvent(Long tradeId, AuditAction action, AuditDetails details) {
        // Enforce referential integrity at the application layer
        tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found with id: " + tradeId));
        TradeAudit audit = new TradeAudit();
        audit.setTradeId(tradeId);
        audit.setAction(action);
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
