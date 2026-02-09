package com.pgim.portfolio.dto;

import com.pgim.portfolio.entity.Trade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeDTO {
    private Long id;

    @NotNull
    private Long portfolioId;

    @NotNull
    private String tradeReferenceId;

    private Trade.TradeType tradeType;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    @Positive
    private BigDecimal price;

    private Trade.TradeStatus status;

    private LocalDateTime createdAt;
}