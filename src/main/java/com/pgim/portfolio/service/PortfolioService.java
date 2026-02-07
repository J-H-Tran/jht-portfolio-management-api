package com.pgim.portfolio.service;

import com.pgim.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.dto.PortfolioMapper;
import com.pgim.portfolio.entity.Portfolio;
import com.pgim.portfolio.repository.PortfolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {
    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;

    //@Autowired - implicit
    public PortfolioService(
            PortfolioRepository portfolioRepository,
            PortfolioMapper portfolioMapper
    ) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
    }

    public Page<PortfolioDTO> getAllPortfolios(Pageable pageable) {
        logger.info("Getting all portfolios");
        return portfolioRepository.findAll(pageable)
                .map(portfolioMapper::toDTO);
    }

    public PortfolioDTO createPortfolio(PortfolioDTO portfolioDTO) {
        Portfolio portfolio = portfolioMapper.toEntity(portfolioDTO);
        return portfolioMapper.toDTO(portfolioRepository.save(portfolio));
    }
}