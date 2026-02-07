package com.pgim.portfolio.dto;

import com.pgim.portfolio.entity.Portfolio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    PortfolioDTO toDTO(Portfolio portfolio);
    Portfolio toEntity(PortfolioDTO portfolioDTO);
}