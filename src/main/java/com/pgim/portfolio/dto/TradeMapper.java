package com.pgim.portfolio.dto;

import com.pgim.portfolio.entity.Trade;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeMapper {
    TradeDTO toDTO(Trade trade);
    Trade toEntity(TradeDTO tradeDTO);
}