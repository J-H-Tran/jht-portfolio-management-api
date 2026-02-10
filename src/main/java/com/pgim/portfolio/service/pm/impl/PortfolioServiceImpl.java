package com.pgim.portfolio.service.pm.impl;

import com.pgim.portfolio.domain.dto.pm.PortfolioDTO;
import com.pgim.portfolio.domain.PortfolioMapper;
import com.pgim.portfolio.domain.dto.pm.TradeDTO;
import com.pgim.portfolio.domain.entity.pm.Portfolio;
import com.pgim.portfolio.domain.entity.pm.Trade;
import com.pgim.portfolio.repository.pm.PortfolioRepository;
import com.pgim.portfolio.service.pm.PortfolioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    // Logger is used for tracking service operations and debugging
    private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    // Constructor injection is preferred for immutability and easier testing
    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;

    // @Autowired is implicit for single constructor
    public PortfolioServiceImpl (
            PortfolioRepository portfolioRepository,
            PortfolioMapper portfolioMapper
    ) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
    }

    /**
     * Fetches all portfolios with pagination support.
     * Uses repository and mapper to convert entities to DTOs.
     */
    public Page<PortfolioDTO> getAllPortfolios(Pageable pageable) {
        logger.info("Getting all portfolios");
        List<Portfolio> portfolios = portfolioRepository.findAllWithTrades();
        List<PortfolioDTO> portfolioDTOs = portfolios.stream()
                .map(portfolioMapper::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(portfolioDTOs, pageable, portfolioDTOs.size());
    }

    /**
     * Retrieves a portfolio by its ID, throws if not found.
     */
    public PortfolioDTO getPortfolioById(Long portfolioId) {
        Portfolio existingPortfolio = portfolioRepository.findByIdWithTrades(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with id: " + portfolioId));
        return portfolioMapper.toDTO(existingPortfolio);
    }

    /**
     * Creates a new portfolio. Links trades to the portfolio entity before saving.
     * This ensures bidirectional mapping and correct persistence.
     */
    public PortfolioDTO createPortfolio(PortfolioDTO portfolioDTO) {
        Portfolio portfolio = portfolioMapper.toEntity(portfolioDTO);

        // Link trades to the portfolio for correct JPA relationship
        if (portfolio.getTrades() != null) {
            portfolio.getTrades().forEach(trade -> trade.setPortfolio(portfolio));
        }
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toDTO(savedPortfolio);
    }

    /**
     * Updates an existing portfolio and its trades.
     * @Transactional ensures atomicity and consistency for the upsert logic.
     * Existing trades are updated, new trades are added, and all are linked to the portfolio.
     */
    @Transactional(transactionManager = "pmTransactionManager") // Ensures all changes are committed or rolled back together
    public PortfolioDTO updatePortfolio(Long portfolioId, PortfolioDTO portfolioDTO) {
        // Fetch the existing portfolio, throws if not found
        Portfolio portfolio = getExistingPortfolio(portfolioId);

        // Update portfolio fields (currently only name)
        portfolio.setName(portfolioDTO.getName());

        // Handle null trades gracefully, default to empty list
        List<TradeDTO> tradeDTOs = portfolioDTO.getTrades() != null ? portfolioDTO.getTrades() : new ArrayList<>();

        // Map existing trades by ID for efficient lookup and update
        Map<Long, Trade> existingTrades = portfolio.getTrades().stream()
                .collect(Collectors.toMap(Trade::getId, trade -> trade));

        // Iterate over incoming trades: update existing or add new
        tradeDTOs.forEach(tradeDTO -> {
            Trade trade = existingTrades.get(tradeDTO.getId());
            if (trade != null) {
                // Update fields for existing trade
                trade.setPrice(tradeDTO.getPrice());
                trade.setQuantity(tradeDTO.getQuantity());
                trade.setStatus(tradeDTO.getStatus());
                trade.setTradeReferenceId(tradeDTO.getTradeReferenceId());
                trade.setTradeType(tradeDTO.getTradeType());
            } else {
                // Add new trade and link to portfolio
                Trade newTrade = new Trade();
                newTrade.setPrice(tradeDTO.getPrice());
                newTrade.setQuantity(tradeDTO.getQuantity());
                newTrade.setStatus(tradeDTO.getStatus());
                newTrade.setTradeReferenceId(tradeDTO.getTradeReferenceId());
                newTrade.setTradeType(tradeDTO.getTradeType());
                newTrade.setPortfolio(portfolio); // Maintain relationship
                portfolio.getTrades().add(newTrade);
            }
        });
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toDTO(savedPortfolio);
    }

    /**
     * Deletes a portfolio by ID. Throws if not found.
     */
    public void deletePortfolio(Long portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new IllegalArgumentException("Portfolio not found with id: " + portfolioId);
        }
        portfolioRepository.deleteById(portfolioId);
    }

    /**
     * Helper method to fetch portfolio or throw if not found.
     * Used to centralize error handling.
     */
    private Portfolio getExistingPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with id: " + portfolioId));
    }
}