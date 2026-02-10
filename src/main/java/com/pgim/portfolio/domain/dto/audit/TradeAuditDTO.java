package com.pgim.portfolio.domain.dto.audit;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TradeAuditDTO {
    private Long id;
    private Long tradeId;
    private String action;
    private String details;
    private LocalDateTime createdAt;
}