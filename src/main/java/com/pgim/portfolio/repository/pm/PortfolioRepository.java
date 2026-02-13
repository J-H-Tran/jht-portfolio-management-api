package com.pgim.portfolio.repository.pm;

import com.pgim.portfolio.domain.entity.pm.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Page<Portfolio> findAll(Pageable pageable);

    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.trades")
    Page<Portfolio> findAllWithTrades(Pageable pageable);

    @Query("select p from Portfolio p left join fetch p.trades where p.id = :id")
    Optional<Portfolio> findByIdWithTrades(@Param("id") Long id);
}