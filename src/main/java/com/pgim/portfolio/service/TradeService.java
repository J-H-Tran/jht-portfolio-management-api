package com.pgim.portfolio.service;

import com.pgim.portfolio.domain.trade.dto.TradeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradeService {
    TradeDTO getTradeById(Long tradeId);
    Page<TradeDTO> getTradesByPortfolioId(Long portfolioId, Pageable pageable);
    Page<TradeDTO> getAllTrades(Pageable pageable);
    TradeDTO addTrade(TradeDTO tradeDTO);
    TradeDTO submitTrade(TradeDTO tradeDTO);
    TradeDTO updateTrade(Long id, TradeDTO updateTradeDTO);
    void deleteTrade(Long id);
}