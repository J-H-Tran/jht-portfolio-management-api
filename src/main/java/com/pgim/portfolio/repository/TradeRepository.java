package com.pgim.portfolio.repository;

import com.pgim.portfolio.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Page<Trade> findByPortfolioId(Long portfolioId, Pageable pageable);
    Optional<Trade> findByTradeReferenceId(String tradeReferenceId);
}