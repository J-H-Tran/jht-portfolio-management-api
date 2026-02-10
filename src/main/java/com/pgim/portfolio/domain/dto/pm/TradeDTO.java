package com.pgim.portfolio.domain.dto.pm;

import com.pgim.portfolio.domain.entity.pm.Trade.TradeStatus;
import com.pgim.portfolio.domain.entity.pm.Trade.TradeType;
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

    private TradeType tradeType;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    @Positive
    private BigDecimal price;

    private TradeStatus status;

    private LocalDateTime createdAt;
}