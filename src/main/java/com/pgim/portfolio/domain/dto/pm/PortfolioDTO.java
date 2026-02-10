package com.pgim.portfolio.domain.dto.pm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PortfolioDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private List<TradeDTO> trades = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}