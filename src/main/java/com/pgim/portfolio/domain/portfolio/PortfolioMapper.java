package com.pgim.portfolio.domain.portfolio;

import com.pgim.portfolio.domain.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.domain.portfolio.entity.Portfolio;
import com.pgim.portfolio.domain.trade.TradeMapper;
import org.mapstruct.Mapper;

/**
 * PortfolioMapper is a MapStruct interface for converting between Portfolio entity and PortfolioDTO.
 *
 * Why MapStruct? MapStruct generates type-safe, performant mappers at compile time, reducing boilerplate and
 * ensuring consistency. This is preferred over manual mapping for maintainability and performance.
 *
 * Why uses = TradeMapper.class? This tells MapStruct to use TradeMapper for mapping nested Trade objects,
 * ensuring that fields like portfolioId in TradeDTO are correctly set. This is essential for nested DTO mapping.
 *
 * Example usage:
 *   PortfolioDTO dto = portfolioMapper.toDTO(portfolio);
 *   Portfolio entity = portfolioMapper.toEntity(dto);
 */
@Mapper(componentModel = "spring", uses = TradeMapper.class)
public interface PortfolioMapper {
    /**
     * Maps Portfolio entity to PortfolioDTO, including nested trades.
     */
    PortfolioDTO toDTO(Portfolio portfolio);

    /**
     * Maps PortfolioDTO to Portfolio entity, including nested trades.
     */
    Portfolio toEntity(PortfolioDTO portfolioDTO);
}