package com.pgim.portfolio.dto;

import org.mapstruct.Mapper;
import com.pgim.portfolio.entity.Portfolio;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    PortfolioDTO toDTO(Portfolio portfolio);
    Portfolio toEntity(PortfolioDTO portfolioDTO);
}