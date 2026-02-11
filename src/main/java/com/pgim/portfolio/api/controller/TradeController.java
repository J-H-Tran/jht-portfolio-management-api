package com.pgim.portfolio.api.controller;

import com.pgim.portfolio.domain.dto.pm.TradeDTO;
import com.pgim.portfolio.service.pm.TradeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

/**
 * Controller for Trade operations.
 * Endpoints are lean, business logic is delegated to service layer.
 * Constructor injection is used for easier testing and immutability.
 */
@RestController
@RequestMapping("v1/api/trades")
public class TradeController {

    private final TradeService tradeService;

    //@Autowired is implicit for single constructor
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping
    public ResponseEntity<Page<TradeDTO>> getTrades(
            Pageable pageable,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(tradeService.getAllTrades(pageable, status));
    }

    /**
     * GET endpoint for trade by ID.
     * Returns 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TradeDTO> getTradeById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeService.getTradeById(id));
    }

    /**
     * GET endpoint for trades by portfolio ID with pagination.
     * Delegates to service for business logic.
     */
    @GetMapping("/portfolio/{id}")
    public ResponseEntity<Page<TradeDTO>> getTradesByPortfolioId(
            Pageable pageable,
            @RequestParam(required = false) String status,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                tradeService.getTradesByPortfolioId(pageable, status, id)
        );
    }

    /**
     * POST endpoint for submitting a trade (idempotency check).
     * Delegates to service for business logic.
     */
    @PostMapping
    public ResponseEntity<TradeDTO> submitTrade(@Valid @RequestBody TradeDTO tradeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tradeService.submitTrade(tradeDTO));
    }

    /**
     * PUT endpoint for updating a trade.
     * Delegates to service for business logic.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TradeDTO> updateTrade(@PathVariable Long id, @RequestBody TradeDTO tradeDTO) {
        return ResponseEntity.ok(tradeService.updateTrade(id, tradeDTO));
    }

    /**
     * DELETE endpoint for removing a trade.
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
        return ResponseEntity.noContent().build();
    }
}