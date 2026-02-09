package com.pgim.portfolio.service;

import com.pgim.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.dto.PortfolioMapper;
import com.pgim.portfolio.dto.TradeDTO;
import com.pgim.portfolio.entity.Portfolio;
import com.pgim.portfolio.entity.Trade;
import com.pgim.portfolio.repository.PortfolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public PortfolioDTO getPortfolioById(Long portfolioId) {
        Portfolio existingPortfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with id: " + portfolioId));
        return portfolioMapper.toDTO(existingPortfolio);
    }

    public PortfolioDTO createPortfolio(PortfolioDTO portfolioDTO) {
        Portfolio portfolio = portfolioMapper.toEntity(portfolioDTO);

        // Link trades to the portfolio
        if (portfolio.getTrades() != null) {
            portfolio.getTrades().forEach(trade -> trade.setPortfolio(portfolio));
        }
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toDTO(savedPortfolio);
    }

    @Transactional
    public PortfolioDTO updatePortfolio(Long portfolioId, PortfolioDTO portfolioDTO) {
        // Fetch the existing portfolio
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with id: " + portfolioId));

        // Update portfolio fields
        portfolio.setName(portfolioDTO.getName());

        // Handle null trades
        List<TradeDTO> tradeDTOs = portfolioDTO.getTrades() != null ? portfolioDTO.getTrades() : new ArrayList<>();

        // Map existing trades by ID for efficient lookup
        Map<Long, Trade> existingTrades = portfolio.getTrades().stream()
                .collect(Collectors.toMap(Trade::getId, trade -> trade));

        // Iterate over the incoming trades and update or add them
        for (TradeDTO tradeDTO : tradeDTOs) {
            Trade trade = existingTrades.get(tradeDTO.getId());
            if (trade != null) {
                // Update existing trade
                trade.setPrice(tradeDTO.getPrice());
                trade.setQuantity(tradeDTO.getQuantity());
                trade.setStatus(tradeDTO.getStatus());
                trade.setTradeReferenceId(tradeDTO.getTradeReferenceId());
                trade.setTradeType(tradeDTO.getTradeType());
            } else {
                // Add new trade
                Trade newTrade = new Trade();
                newTrade.setPrice(tradeDTO.getPrice());
                newTrade.setQuantity(tradeDTO.getQuantity());
                newTrade.setStatus(tradeDTO.getStatus());
                newTrade.setTradeReferenceId(tradeDTO.getTradeReferenceId());
                newTrade.setTradeType(tradeDTO.getTradeType());
                newTrade.setPortfolio(portfolio);
                portfolio.getTrades().add(newTrade);
            }
        }

        // Save the portfolio
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toDTO(savedPortfolio);
    }

    public void deletePortfolio(Long portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new IllegalArgumentException("Portfolio not found with id: " + portfolioId);
        }
        portfolioRepository.deleteById(portfolioId);
    }
}