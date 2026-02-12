package com.pgim.portfolio.domain;

import com.pgim.portfolio.domain.dto.audit.TradeAuditDTO;
import com.pgim.portfolio.domain.entity.audit.TradeAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * TradeAuditMapper is a MapStruct interface for converting between TradeAudit entity and TradeAuditDTO.
 *
 * Why MapStruct? MapStruct generates type-safe, performant mappers at compile time, reducing boilerplate and
 * ensuring consistency. This is preferred over manual mapping for maintainability and performance.
 *
 * Example usage:
 *   TradeAuditDTO dto = tradeAuditMapper.toDTO(tradeAudit);
 *   TradeAudit entity = tradeAuditMapper.toEntity(dto);
 */
@Mapper(componentModel = "spring")
public interface TradeAuditMapper {
    /**
     * Maps TradeAudit entity to TradeAuditDTO.
     */
    TradeAuditDTO toDTO(TradeAudit tradeAudit);

    /**
     * Maps TradeAuditDTO to TradeAudit entity.
     * Ignores id, createdAt, and updatedAt fields to prevent overwriting system-managed values.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TradeAudit toEntity(TradeAuditDTO tradeAuditDTO);
}