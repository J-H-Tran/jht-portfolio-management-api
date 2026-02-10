package com.pgim.portfolio.domain.portfolio;

import com.pgim.portfolio.domain.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.domain.portfolio.entity.Portfolio;
import com.pgim.portfolio.domain.trade.TradeMapper;
import org.mapstruct.Mapper;

/**
 * Adding `uses = TradeMapper.class` in your PortfolioMapper ensures MapStruct uses your custom mapping for
 * nested trades,so portfolioId is correctly set in each TradeDTO. This is the recommended approach for
 * nested DTO mapping with MapStruct.
 * */
@Mapper(componentModel = "spring", uses = TradeMapper.class)
public interface PortfolioMapper {
    PortfolioDTO toDTO(Portfolio portfolio);
    Portfolio toEntity(PortfolioDTO portfolioDTO);
}