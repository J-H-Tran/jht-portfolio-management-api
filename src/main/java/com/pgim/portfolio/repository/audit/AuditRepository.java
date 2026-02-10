package com.pgim.portfolio.repository.audit;


import com.pgim.portfolio.domain.entity.audit.TradeAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<TradeAudit, Long> {
}