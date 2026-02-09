package com.pgim.portfolio.controller;

import com.pgim.portfolio.dto.TradeDTO;
import com.pgim.portfolio.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<Page<TradeDTO>> getTradesByPortfolioId(
            @PathVariable Long portfolioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TradeDTO> tradesByPortfolioId = tradeService.getTradesByPortfolioId(portfolioId, PageRequest.of(page, size));
        return ResponseEntity.ok(tradesByPortfolioId);
    }

    @PostMapping("/save")
    public ResponseEntity<TradeDTO> addTrade(@Valid @RequestBody TradeDTO tradeDTO) {
        TradeDTO createdTrade = tradeService.addTrade(tradeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTrade);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TradeDTO> updateTrade(@PathVariable Long id, @RequestBody TradeDTO tradeDTO) {
        TradeDTO updatedTrade = tradeService.updateTrade(id, tradeDTO);
        return ResponseEntity.ok(updatedTrade);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
        return ResponseEntity.noContent().build();
    }
}