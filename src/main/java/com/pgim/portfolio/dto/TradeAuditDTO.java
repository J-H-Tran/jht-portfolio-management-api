package com.pgim.portfolio.dto;

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