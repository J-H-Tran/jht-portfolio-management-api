package com.pgim.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PortfolioDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 30, message = "Name must not exceed 50 characters")
    private String name;

    private String description;
}