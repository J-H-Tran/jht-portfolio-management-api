package com.pgim.portfolio.service;

import com.pgim.portfolio.dto.TradeDTO;
import com.pgim.portfolio.dto.TradeMapper;
import com.pgim.portfolio.entity.Trade;
import com.pgim.portfolio.repository.TradeRepository;
import java.math.BigDecimal;
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
//    private final MessageQueuePublisher messageQueuePublisher;

    public TradeService(
            TradeRepository tradeRepository,
            TradeMapper tradeMapper
    ) {
        this.tradeRepository = tradeRepository;
        this.tradeMapper = tradeMapper;
    }

    public TradeDTO getTradeById(Long tradeId) {
        Trade existingTrade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with id: " + tradeId));
        return tradeMapper.toDTO(existingTrade);
    }

    public Page<TradeDTO> getTradesByPortfolioId(Long portfolioId, Pageable pageable) {
        logger.info("Fetching trades for portfolioId: {}", portfolioId);
        return tradeRepository.findByPortfolioId(portfolioId, pageable)
                .map(tradeMapper::toDTO);
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

    public TradeDTO submitTrade(TradeDTO tradeDTO) {
        // Idempotency check
        if (tradeRepository.findByTradeReferenceId(tradeDTO.getTradeReferenceId()).isPresent()) {
            throw new IllegalArgumentException("Duplicate trade submission with reference ID: " + tradeDTO.getTradeReferenceId());
        }

        validateTrade(tradeDTO);    // validate trade details

        Trade trade = tradeMapper.toEntity(tradeDTO);

        Trade savedTrade = tradeRepository.save(trade);

        // Publish trade to the database
//        messageQueuePublisher.publishTrade(savedTrade);
        // log submission
        logger.info("Trade submitted successfully: {}", savedTrade);
        return tradeMapper.toDTO(savedTrade);
    }

    public TradeDTO updateTrade(Long id, TradeDTO updateTradeDTO) {
        return tradeRepository.findById(id)
                .map(existingTrade -> {
                    Trade updateTrade = tradeMapper.toEntity(updateTradeDTO);
                    updateTrade.setId(existingTrade.getId()); // Preserve ID
                    updateTrade.setPortfolio(existingTrade.getPortfolio()); // Preserve portfolio relationship
                    Trade savedTrade = tradeRepository.save(updateTrade);
                    return tradeMapper.toDTO(savedTrade);
                })
                .orElseThrow(() -> new IllegalArgumentException("Trade not found with id: " + id));
    }

    public void deleteTrade(Long id) {
        tradeRepository.deleteById(id);
    }

    private void validateTrade(TradeDTO tradeDTO) {
        if (tradeDTO.getQuantity() == null || tradeDTO.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade quantity must be greater than zero.");
        }
        if (tradeDTO.getPrice() == null || tradeDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade price must be greater than zero.");
        }
        if (tradeDTO.getTradeType() == null) {
            throw new IllegalArgumentException("Trade type must be specified.");
        }
    }
}