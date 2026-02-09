package com.pgim.portfolio.controller;

import com.pgim.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.service.PortfolioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // under the hood is annotated with @Controller @ResponseBody
@RequestMapping("/api/portfolios")
public class PortfolioController {
    // CRUD: Create, Read, Update, Delete
    private final PortfolioService portfolioService;

    //@Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // Equivalent -> @RequestMapping(value=”/home”, method = RequestMethod.GET), Read
    @GetMapping
    public Page<PortfolioDTO> getAllPortfolios(Pageable pageable) {
        return portfolioService.getAllPortfolios(pageable);
    }

    // Create
    @PostMapping(value = "/save")
    public PortfolioDTO createPortfolio(@RequestBody PortfolioDTO portfolioDTO) {
        return portfolioService.createPortfolio(portfolioDTO);
    }

    // Update - to update existing data
    @PutMapping(value = "/update/{id}")
    public PortfolioDTO updatePortfolio(
            @PathVariable("id") Long portfolioId,
            @RequestBody PortfolioDTO portfolioDTO
    ) {
        return portfolioService.updatePortfolio(portfolioId, portfolioDTO);
    }

    @DeleteMapping(value = "/delete/{id}")
    public void deletePortfolio(@PathVariable("id") Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
    }
}