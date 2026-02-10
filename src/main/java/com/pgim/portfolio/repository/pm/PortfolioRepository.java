package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}