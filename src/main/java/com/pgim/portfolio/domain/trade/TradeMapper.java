package com.pgim.portfolio.domain.trade;

import com.pgim.portfolio.domain.trade.dto.TradeDTO;
import com.pgim.portfolio.domain.trade.entity.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Bean for spring to manage, update pom xml
public interface TradeMapper {
    @Mapping(source = "portfolio.id", target = "portfolioId")
    TradeDTO toDTO(Trade trade);

    @Mapping(source = "portfolioId", target = "portfolio.id")
    Trade toEntity(TradeDTO tradeDTO);
}