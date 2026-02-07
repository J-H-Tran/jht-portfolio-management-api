package com.pgim.portfolio.dto;

import com.pgim.portfolio.entity.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Bean for spring to manage, update pom xml
public interface TradeMapper {
    @Mapping(source = "portfolio.id", target = "portfolioId")
    TradeDTO toDTO(Trade trade);

    @Mapping(source = "portfolioId", target = "portfolio.id")
    Trade toEntity(TradeDTO tradeDTO);
}