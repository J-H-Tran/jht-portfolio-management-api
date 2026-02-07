package com.pgim.portfolio.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeDTO {
    @NotNull
    private String assetName;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private Long portfolioId;
}