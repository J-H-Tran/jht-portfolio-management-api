package com.pgim.portfolio.domain;

import com.pgim.portfolio.domain.dto.pm.PortfolioDTO;
import com.pgim.portfolio.domain.entity.pm.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
     * Ignores id, createdAt, and updatedAt fields to prevent overwriting system-managed values.
     * Ignores trades field for clarity and to avoid unintended side effects.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trades", ignore = true) // trades are managed separately or by nested mapping
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Portfolio toEntity(PortfolioDTO portfolioDTO);
}