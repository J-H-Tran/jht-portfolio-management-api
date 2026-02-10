package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.trades")
    List<Portfolio> findAllWithTrades();

    @Query("select p from Portfolio p left join fetch p.trades where p.id = :id")
    Optional<Portfolio> findByIdWithTrades(@Param("id") Long id);
}