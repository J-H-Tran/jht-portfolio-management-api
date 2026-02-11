package com.pgim.portfolio.service.pm.impl;

import com.pgim.portfolio.domain.TradeMapper;
import com.pgim.portfolio.domain.dto.pm.TradeDTO;
import com.pgim.portfolio.domain.entity.pm.Trade;
import com.pgim.portfolio.repository.pm.TradeRepository;
import com.pgim.portfolio.service.audit.TradeAuditService;
import com.pgim.portfolio.service.pm.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.pgim.portfolio.domain.entity.audit.TradeAudit.AuditAction.DELETE;
import static com.pgim.portfolio.domain.entity.audit.TradeAudit.AuditAction.SUBMIT;
import static com.pgim.portfolio.domain.entity.audit.TradeAudit.AuditAction.UPDATE;

@Service
public class TradeServiceImpl implements TradeService {
    // Logger is used for tracking service operations and debugging
    private static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);

    // Constructor injection is preferred for immutability and easier testing
    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;
    private final TradeAuditService tradeAuditService;
//    private final MessageQueuePublisher messageQueuePublisher;

    // @Autowired is implicit for single constructor
    public TradeServiceImpl(
            TradeRepository tradeRepository,
            TradeMapper tradeMapper,
            TradeAuditService tradeAuditService
    ) {
        this.tradeRepository = tradeRepository;
        this.tradeMapper = tradeMapper;
        this.tradeAuditService = tradeAuditService;
    }

    @Override
    public Page<TradeDTO> getAllTrades(Pageable pageable, String status) {
        if (status != null) {
            return tradeRepository.findByStatus(pageable, status)
                    .map(tradeMapper::toDTO);
        }
        return tradeRepository.findAll(pageable)
                .map(tradeMapper::toDTO);
    }

    /**
     * Fetches a trade by its ID, throws if not found.
     */
    public TradeDTO getTradeById(Long tradeId) {
        Trade existingTrade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with id: " + tradeId));
        return tradeMapper.toDTO(existingTrade);
    }

    /**
     * Fetches trades for a portfolio with pagination.
     * Uses repository and mapper to convert entities to DTOs.
     */
    public Page<TradeDTO> getTradesByPortfolioId(Pageable pageable, String status, Long id) {
        logger.info("Fetching trades for portfolioId: {}, status: {}", id, status);
        if (status != null) {
            return tradeRepository.findByPortfolioIdAndStatus(pageable, status, id)
                    .map(tradeMapper::toDTO);
        }
        return tradeRepository.findByPortfolioId(id, pageable)
                .map(tradeMapper::toDTO);
    }

    /**
     * Submits a trade with idempotency check.
     * Throws if duplicate reference ID is found.
     * Validates trade details before persisting.
     */
    public TradeDTO submitTrade(TradeDTO tradeDTO) {
        // Idempotency check
        if (tradeRepository.findByTradeReferenceId(tradeDTO.tradeReferenceId()).isPresent()) {
            throw new IllegalArgumentException("Duplicate trade submission with reference ID: " + tradeDTO.tradeReferenceId());
        }

        // validate trade details
        validateTrade(tradeDTO);

        Trade trade = tradeMapper.toEntity(tradeDTO);
        Trade savedTrade = tradeRepository.save(trade);

        // Publish trade to the database
//        messageQueuePublisher.publishTrade(savedTrade);
        // Log the trade submission in audit table
        tradeAuditService.logTradeEvent(
            savedTrade.getId(),
            SUBMIT,
            "Trade submitted successfully with reference ID: " + savedTrade.getTradeReferenceId()
        );
        // log submission
        logger.info("Trade submitted successfully: {}", savedTrade);
        return tradeMapper.toDTO(savedTrade);
    }

    /**
     * Updates an existing trade. Preserves ID and portfolio relationship.
     */
    public TradeDTO updateTrade(Long id, TradeDTO updateTradeDTO) {
        Trade existingTrade = tradeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Trade not found for trade id: " + id)
        );
        tradeAuditService.logTradeEvent(
                id,
                UPDATE,
                "Trade update initiated with reference ID: " + existingTrade.getTradeReferenceId()
        );
        // Update trade details
        Trade updateTrade = tradeMapper.toEntity(updateTradeDTO);
        updateTrade.setId(existingTrade.getId()); // Preserve ID
        updateTrade.setPortfolio(existingTrade.getPortfolio()); // Preserve portfolio relationship
        Trade savedTrade = tradeRepository.save(updateTrade);

        tradeAuditService.logTradeEvent(
                id,
                UPDATE,
                "Trade updated successfully with reference ID: " + updateTrade.getTradeReferenceId()
        );
        return tradeMapper.toDTO(savedTrade);
    }

    /**
     * Deletes a trade by ID.
     */
    public void deleteTrade(Long id) {
        tradeAuditService.logTradeEvent(
                id,
                DELETE,
                "Trade deleted successfully with reference ID: " + id
        );
        tradeRepository.deleteById(id);
    }

    /**
     * Validates trade details for business rules.
     * Throws if invalid.
     */
    private void validateTrade(TradeDTO tradeDTO) {
        if (tradeDTO.quantity() == null || tradeDTO.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade quantity must be greater than zero.");
        }
        if (tradeDTO.price() == null || tradeDTO.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Trade price must be greater than zero.");
        }
        if (tradeDTO.tradeType() == null) {
            throw new IllegalArgumentException("Trade type must be specified.");
        }
    }
}