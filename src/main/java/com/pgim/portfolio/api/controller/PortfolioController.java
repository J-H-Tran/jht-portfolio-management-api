package com.pgim.portfolio.api.controller;

import com.pgim.portfolio.domain.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.service.impl.PortfolioServiceImpl;
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

@RestController // under the hood is annotated with @Controller @ResponseBody
@RequestMapping("v1/api/portfolios")
public class PortfolioController {
    // CRUD: Create, Read, Update, Delete
    private final PortfolioServiceImpl portfolioServiceImpl;
    private final PagedResourcesAssembler<PortfolioDTO> pagedResourcesAssembler;

    //@Autowired
    public PortfolioController(
            PortfolioServiceImpl portfolioServiceImpl,
            PagedResourcesAssembler<PortfolioDTO> pagedResourcesAssembler
    ) {
        this.portfolioServiceImpl = portfolioServiceImpl;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    // Equivalent -> @RequestMapping(value=”/home”, method = RequestMethod.GET), Read
    @GetMapping
    public ResponseEntity<Page<PortfolioDTO>> getAllPortfolios(Pageable pageable) {
        Page<PortfolioDTO> portfolios = portfolioServiceImpl.getAllPortfolios(pageable);
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Long id) {
        PortfolioDTO portfolio = portfolioServiceImpl.getPortfolioById(id);
        return ResponseEntity.ok(portfolio);
    }

    // Create
    @PostMapping(value = "/save")
    public ResponseEntity<PortfolioDTO> createPortfolio(@Valid @RequestBody PortfolioDTO portfolioDTO) {
        PortfolioDTO createdPortfolio = portfolioServiceImpl.createPortfolio(portfolioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
    }

    // Update - to update existing data
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(
            @PathVariable("id") Long portfolioId,
            @RequestBody PortfolioDTO portfolioDTO
    ) {
        PortfolioDTO updatedPortfolio = portfolioServiceImpl.updatePortfolio(portfolioId, portfolioDTO);
        return ResponseEntity.ok(updatedPortfolio);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable("id") Long portfolioId) {
        portfolioServiceImpl.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }
}