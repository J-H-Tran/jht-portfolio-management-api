package com.pgim.portfolio.domain.impl;

import com.pgim.portfolio.domain.TradeAuditMapper;
import com.pgim.portfolio.domain.dto.audit.TradeAuditDTO;
import com.pgim.portfolio.domain.entity.audit.AuditDetails;
import com.pgim.portfolio.domain.entity.audit.TradeAudit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TradeAuditMapperImpl implements TradeAuditMapper {
    public TradeAuditDTO toDTO(TradeAudit tradeAudit) {
        if (tradeAudit == null) {
            return null;
        } else {
            Long id = null;
            Long tradeId = null;
            String action = null;
            AuditDetails details = null;
            LocalDateTime createdAt = null;
            id = tradeAudit.getId();
            tradeId = tradeAudit.getTradeId();
            if (tradeAudit.getAction() != null) {
                action = tradeAudit.getAction().name();
            }

            details = tradeAudit.getDetails();
            createdAt = tradeAudit.getCreatedAt();
            return new TradeAuditDTO(id, tradeId, action, details, createdAt);
        }
    }

    public TradeAudit toEntity(TradeAuditDTO tradeAuditDTO) {
        if (tradeAuditDTO == null) {
            return null;
        } else {
            TradeAudit tradeAudit = new TradeAudit();
            tradeAudit.setTradeId(tradeAuditDTO.tradeId());
            if (tradeAuditDTO.action() != null) {
                tradeAudit.setAction((TradeAudit.AuditAction)Enum.valueOf(TradeAudit.AuditAction.class, tradeAuditDTO.action()));
            }

            tradeAudit.setDetails(tradeAuditDTO.details());
            return tradeAudit;
        }
    }
}
