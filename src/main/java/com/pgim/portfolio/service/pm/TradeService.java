package com.pgim.portfolio.service.pm;

import com.pgim.portfolio.domain.dto.pm.TradeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradeService {
    Page<TradeDTO> getAllTrades(Pageable pageable, String status);
    TradeDTO getTradeById(Long tradeId);
    Page<TradeDTO> getTradesByPortfolioId(Pageable pageable, String status, Long id);
    TradeDTO submitTrade(TradeDTO tradeDTO);
    TradeDTO updateTrade(Long id, TradeDTO updateTradeDTO);
    void deleteTrade(Long id);
}