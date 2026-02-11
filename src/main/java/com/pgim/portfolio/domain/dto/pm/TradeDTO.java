package com.pgim.portfolio.domain.dto.pm;

import com.pgim.portfolio.domain.entity.pm.Trade.TradeStatus;
import com.pgim.portfolio.domain.entity.pm.Trade.TradeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeDTO (
    Long id,

    @NotNull
    Long portfolioId,

    @NotNull
    String tradeReferenceId,

    TradeType tradeType,

    @NotNull
    @Positive
    BigDecimal quantity,

    @NotNull
    @Positive
    BigDecimal price,

    TradeStatus status,

    LocalDateTime createdAt
) {}