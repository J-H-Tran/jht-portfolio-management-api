package com.pgim.portfolio.domain.impl;

import com.pgim.portfolio.domain.PortfolioMapper;
import com.pgim.portfolio.domain.TradeMapper;
import com.pgim.portfolio.domain.dto.pm.PortfolioDTO;
import com.pgim.portfolio.domain.dto.pm.TradeDTO;
import com.pgim.portfolio.domain.entity.pm.Portfolio;
import com.pgim.portfolio.domain.entity.pm.Trade;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PortfolioMapperImpl implements PortfolioMapper {
    private final TradeMapper tradeMapper;

    public PortfolioMapperImpl(TradeMapper tradeMapper) {
        this.tradeMapper = tradeMapper;
    }

    public PortfolioDTO toDTO(Portfolio portfolio) {
        if (portfolio == null) {
            return null;
        } else {
            Long id = null;
            String name = null;
            List<TradeDTO> trades = null;
            LocalDateTime createdAt = null;
            LocalDateTime updatedAt = null;
            id = portfolio.getId();
            name = portfolio.getName();
            trades = this.tradeListToTradeDTOList(portfolio.getTrades());
            createdAt = portfolio.getCreatedAt();
            updatedAt = portfolio.getUpdatedAt();
            return new PortfolioDTO(id, name, trades, createdAt, updatedAt);
        }
    }

    public Portfolio toEntity(PortfolioDTO portfolioDTO) {
        if (portfolioDTO == null) {
            return null;
        } else {
            Portfolio portfolio = new Portfolio();
            portfolio.setName(portfolioDTO.name());
            portfolio.setTrades(this.tradeDTOListToTradeList(portfolioDTO.trades()));
            return portfolio;
        }
    }

    protected List<TradeDTO> tradeListToTradeDTOList(List<Trade> list) {
        if (list == null) {
            return null;
        } else {
            List<TradeDTO> list1 = new ArrayList<>(list.size());

            for(Trade trade : list) {
                list1.add(this.tradeMapper.toDTO(trade));
            }

            return list1;
        }
    }

    protected List<Trade> tradeDTOListToTradeList(List<TradeDTO> list) {
        if (list == null) {
            return null;
        } else {
            List<Trade> list1 = new ArrayList<>(list.size());

            for(TradeDTO tradeDTO : list) {
                list1.add(this.tradeMapper.toEntity(tradeDTO));
            }

            return list1;
        }
    }
}
