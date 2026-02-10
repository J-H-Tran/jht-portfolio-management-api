package com.pgim.portfolio.domain.tradeAudit;

import com.pgim.portfolio.domain.tradeAudit.entity.TradeAudit;
import com.pgim.portfolio.domain.tradeAudit.dto.TradeAuditDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeAuditMapper {
    TradeAuditDTO toDTO(TradeAudit tradeAudit);
    TradeAudit toEntity(TradeAuditDTO tradeAuditDTO);
}