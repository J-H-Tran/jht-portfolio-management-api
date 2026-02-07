package com.pgim.portfolio.controller;

import com.pgim.portfolio.dto.PortfolioDTO;
import com.pgim.portfolio.service.PortfolioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    //@Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public Page<PortfolioDTO> getAllPortfolios(Pageable pageable) {
        return portfolioService.getAllPortfolios(pageable);
    }

    @PostMapping
    public PortfolioDTO createPortfolio(@RequestBody PortfolioDTO portfolioDTO) {
        return portfolioService.createPortfolio(portfolioDTO);
    }
}