package com.pgim.portfolio.domain.portfolio;

import com.pgim.portfolio.domain.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.domain.portfolio.entity.Portfolio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    PortfolioDTO toDTO(Portfolio portfolio);
    Portfolio toEntity(PortfolioDTO portfolioDTO);
}