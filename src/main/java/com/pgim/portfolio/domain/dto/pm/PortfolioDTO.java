package com.pgim.portfolio.domain.dto.pm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PortfolioDTO(
    Long id,

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name,

    List<TradeDTO> trades,

    LocalDateTime createdAt,

    LocalDateTime updatedAt
) {}