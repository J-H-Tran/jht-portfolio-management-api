package com.pgim.portfolio.domain.trade;

import com.pgim.portfolio.domain.trade.dto.TradeDTO;
import com.pgim.portfolio.domain.trade.entity.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * TradeMapper is a MapStruct interface for converting between Trade entity and TradeDTO.
 *
 * Why MapStruct? MapStruct generates type-safe, performant mappers at compile time, reducing boilerplate and
 * ensuring consistency. This is preferred over manual mapping for maintainability and performance.
 *
 * Custom @Mapping annotations are used to handle the mapping between the entity's portfolio.id and the DTO's portfolioId field.
 * This is necessary because the entity uses an object reference while the DTO uses a flat ID.
 *
 * Example usage:
 *   TradeDTO dto = tradeMapper.toDTO(trade);
 *   Trade entity = tradeMapper.toEntity(dto);
 */
@Mapper(componentModel = "spring") // Bean for spring to manage, update pom xml
public interface TradeMapper {
    /**
     * Maps Trade entity to TradeDTO, mapping portfolio.id to portfolioId.
     */
    @Mapping(source = "portfolio.id", target = "portfolioId")
    TradeDTO toDTO(Trade trade);

    /**
     * Maps TradeDTO to Trade entity, mapping portfolioId to portfolio.id.
     */
    @Mapping(source = "portfolioId", target = "portfolio.id")
    Trade toEntity(TradeDTO tradeDTO);
}