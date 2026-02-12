package com.pgim.portfolio.domain.entity.audit;

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
    private String note;
    private String referenceId;
    //TODO [Reverse Engineering] generate columns from DB
}