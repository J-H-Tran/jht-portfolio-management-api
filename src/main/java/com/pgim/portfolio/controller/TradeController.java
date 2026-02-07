package com.pgim.portfolio.controller;

import com.pgim.portfolio.dto.TradeDTO;
import com.pgim.portfolio.entity.Trade;
import com.pgim.portfolio.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/portfolio/{portfolioId}")
    public Page<TradeDTO> getTradesByPortfolioId(
            @PathVariable Long portfolioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return tradeService.getTradesByPortfolioId(portfolioId, PageRequest.of(page, size));
    }

    @PostMapping
    public TradeDTO addTrade(@Valid @RequestBody TradeDTO tradeDTO) {
        return tradeService.addTrade(tradeDTO);
    }

    @PutMapping("/{id}")
    public TradeDTO updateTrade(@PathVariable Long id, @RequestBody TradeDTO tradeDTO) {
        return tradeService.updateTrade(id, tradeDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
    }
}