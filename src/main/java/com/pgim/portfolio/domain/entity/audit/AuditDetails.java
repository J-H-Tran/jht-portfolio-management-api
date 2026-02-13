package com.pgim.portfolio.domain.entity.audit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditDetails {
    @NotNull(message = "Note is required")
    private String note;

    @NotNull(message = "Reference ID is required")
    private String referenceId;
}