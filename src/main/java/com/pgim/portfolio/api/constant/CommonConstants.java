package com.pgim.portfolio.api.constant;

public final class CommonConstants {
    private CommonConstants() {}

    // Audit DB Configuration Constants
    public static final String AUDIT_DATASOURCE = "auditDataSource";
    public static final String AUDIT_ENTITY_MANAGER_FACTORY = "auditEntityManagerFactory";
    public static final String AUDIT_TRANSACTION_MANAGER = "auditTransactionManager";
    public static final String AUDIT_ENTITY_PACKAGE = "com.pgim.portfolio.domain.entity.audit";
    public static final String AUDIT_REPOSITORY_PACKAGE = "com.pgim.portfolio.repository.audit";
    public static final String AUDIT_SPRING_DATASOURCE = "spring.datasource.audit";

    // Portfolio DB Configuration Constants
    public static final String PORTFOLIO_DATASOURCE = "pmDataSource";
    public static final String PORTFOLIO_ENTITY_MANAGER_FACTORY = "pmEntityManagerFactory";
    public static final String PORTFOLIO_TRANSACTION_MANAGER = "pmTransactionManager";
    public static final String PORTFOLIO_ENTITY_PACKAGE = "com.pgim.portfolio.domain.entity.pm";
    public static final String PORTFOLIO_REPOSITORY_PACKAGE = "com.pgim.portfolio.repository.pm";

}
