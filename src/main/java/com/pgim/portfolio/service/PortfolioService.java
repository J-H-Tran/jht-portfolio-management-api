package com.pgim.portfolio.service;

import com.pgim.portfolio.entity.Portfolio;
import com.pgim.portfolio.repository.PortfolioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    //@Autowired - implicit
    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public Page<Portfolio> getAllPortfolios(Pageable pageable) {
        return portfolioRepository.findAll(pageable);
    }

    public Portfolio createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }
}