package com.pgim.portfolio.service.pm;

import com.pgim.portfolio.domain.dto.pm.PortfolioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PortfolioService {
    Page<PortfolioDTO> getAllPortfolios(Pageable pageable);
    PortfolioDTO getPortfolioById(Long portfolioId);
    PortfolioDTO createPortfolio(PortfolioDTO portfolioDTO);
    PortfolioDTO updatePortfolio(Long portfolioId, PortfolioDTO portfolioDTO);
    void deletePortfolio(Long portfolioId);
}