package com.pgim.portfolio.dto;

import com.pgim.portfolio.entity.TradeAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TradeAuditMapper {
    TradeAuditDTO toDTO(TradeAudit tradeAudit);
    TradeAudit toEntity(TradeAuditDTO tradeAuditDTO);
}