package com.pgim.portfolio.domain.impl;

import com.pgim.portfolio.domain.TradeMapper;
import com.pgim.portfolio.domain.dto.pm.TradeDTO;
import com.pgim.portfolio.domain.entity.pm.Portfolio;
import com.pgim.portfolio.domain.entity.pm.Trade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TradeMapperImpl implements TradeMapper {
    public TradeDTO toDTO(Trade trade) {
        if (trade == null) {
            return null;
        } else {
            Long portfolioId = null;
            Long id = null;
            String tradeReferenceId = null;
            Trade.TradeType tradeType = null;
            BigDecimal quantity = null;
            BigDecimal price = null;
            Trade.TradeStatus status = null;
            LocalDateTime createdAt = null;
            portfolioId = this.tradePortfolioId(trade);
            id = trade.getId();
            tradeReferenceId = trade.getTradeReferenceId();
            tradeType = trade.getTradeType();
            quantity = trade.getQuantity();
            price = trade.getPrice();
            status = trade.getStatus();
            createdAt = trade.getCreatedAt();
            return new TradeDTO(id, portfolioId, tradeReferenceId, tradeType, quantity, price, status, createdAt);
        }
    }

    public Trade toEntity(TradeDTO tradeDTO) {
        if (tradeDTO == null) {
            return null;
        } else {
            Trade trade = new Trade();
            trade.setPortfolio(this.tradeDTOToPortfolio(tradeDTO));
            trade.setId(tradeDTO.id());
            trade.setTradeReferenceId(tradeDTO.tradeReferenceId());
            trade.setTradeType(tradeDTO.tradeType());
            trade.setQuantity(tradeDTO.quantity());
            trade.setPrice(tradeDTO.price());
            trade.setStatus(tradeDTO.status());
            return trade;
        }
    }

    private Long tradePortfolioId(Trade trade) {
        if (trade == null) {
            return null;
        } else {
            Portfolio portfolio = trade.getPortfolio();
            if (portfolio == null) {
                return null;
            } else {
                return portfolio.getId();
            }
        }
    }

    protected Portfolio tradeDTOToPortfolio(TradeDTO tradeDTO) {
        if (tradeDTO == null) {
            return null;
        } else {
            Portfolio portfolio = new Portfolio();
            portfolio.setId(tradeDTO.portfolioId());
            return portfolio;
        }
    }
}
