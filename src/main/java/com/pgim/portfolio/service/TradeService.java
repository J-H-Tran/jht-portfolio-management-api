package com.pgim.portfolio.service;

import com.pgim.portfolio.dto.TradeDTO;
import com.pgim.portfolio.dto.TradeMapper;
import com.pgim.portfolio.entity.Trade;
import com.pgim.portfolio.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TradeService {
    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;

    public TradeService(
            TradeRepository tradeRepository,
            TradeMapper tradeMapper
    ) {
        this.tradeRepository = tradeRepository;
        this.tradeMapper = tradeMapper;
    }

    public Page<TradeDTO> getTradesByPortfolioId(Long portfolioId, Pageable pageable) {
        logger.info("Fetching trades for portfolioId: {}", portfolioId);
        Page<TradeDTO> trades = tradeRepository.findByPortfolioId(portfolioId, pageable)
                .map(tradeMapper::toDTO);
        logger.info("Fetched trades: {}", trades.getContent());
        return trades;
    }

    public Page<TradeDTO> getAllTrades(Pageable pageable) {
        logger.info("Fetching all trades");
        return tradeRepository.findAll(pageable)
                .map(tradeMapper::toDTO);
    }

    public TradeDTO addTrade(TradeDTO tradeDTO) {
        Trade trade =tradeMapper.toEntity(tradeDTO);

        logger.info("Adding trade: {}", tradeDTO);
        Trade savedTrade = tradeRepository.save(trade);
        return tradeMapper.toDTO(savedTrade);
    }

    public TradeDTO updateTrade(Long id, TradeDTO updateTradeDTO) {
        return tradeRepository.findById(id)
                .map(existingTrade -> {
                    Trade updateTrade = tradeMapper.toEntity(updateTradeDTO);
                    updateTrade.setId(existingTrade.getId()); // Preserve ID
                    updateTrade.setPortfolio(existingTrade.getPortfolio()); // Preserve portfolio relationship
                    return tradeMapper.toDTO(tradeRepository.save(updateTrade));
                })
                .orElseThrow(() -> new IllegalArgumentException("Trade not found with id: " + id));
    }

    public void deleteTrade(Long id) {
        tradeRepository.deleteById(id);
    }
}