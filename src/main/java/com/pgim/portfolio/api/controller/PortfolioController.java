package com.pgim.portfolio.api.controller;

import com.pgim.portfolio.domain.dto.pm.PortfolioDTO;
import com.pgim.portfolio.service.pm.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Portfolio CRUD operations.
 * Keeps endpoints lean, delegates business logic to service layer.
 * Uses constructor injection for easier testing and immutability.
 */
@RestController // under the hood is annotated with @Controller @ResponseBody
@RequestMapping("v1/api/portfolios")
public class PortfolioController {
    // CRUD: Create, Read, Update, Delete
    private final PortfolioService portfolioService;
    private final PagedResourcesAssembler<PortfolioDTO> pagedResourcesAssembler;

    //@Autowired is implicit for single constructor
    public PortfolioController(
            PortfolioService portfolioService,
            PagedResourcesAssembler<PortfolioDTO> pagedResourcesAssembler
    ) {
        this.portfolioService = portfolioService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    /**
     * GET endpoint for paginated portfolios.
     * Delegates to service for business logic.
     * Equivalent annotation: @RequestMapping(value="", method = RequestMethod.GET)
     */
    @GetMapping
    public ResponseEntity<Page<PortfolioDTO>> getAllPortfolios(Pageable pageable) {
        return ResponseEntity.ok(portfolioService.getAllPortfolios(pageable));
    }

    /**
     * GET endpoint for portfolio by ID.
     * Returns 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(
            @PathVariable Long id
    ) {
        PortfolioDTO portfolio = portfolioService.getPortfolioById(id);
        return ResponseEntity.ok(portfolio);
    }

    /**
     * POST endpoint for creating a portfolio.
     * Validates input and delegates creation to service.
     */
    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(
            @Valid @RequestBody PortfolioDTO portfolioDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.createPortfolio(portfolioDTO));
    }

    /**
     * PUT endpoint for updating a portfolio.
     * Uses @Transactional in service for atomic upsert logic.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(
            @PathVariable Long id,
            @RequestBody PortfolioDTO portfolioDTO
    ) {
        return ResponseEntity.ok(portfolioService.updatePortfolio(id, portfolioDTO));
    }

    /**
     * DELETE endpoint for removing a portfolio.
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable Long id
    ) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }
}