package com.pgim.portfolio.api.constant;

public final class CommonConstants {
    private CommonConstants() {}

    // Authorities
    public static final String ROLE_PREFIX = "ROLE_";

    // Portfolio DB Configuration Constants
    public static final String PORTFOLIO_DATASOURCE = "pmDataSource";
    public static final String PORTFOLIO_ENTITY_MANAGER = "pmEntityManagerFactory";
    public static final String PORTFOLIO_TRANSACTION_MANAGER = "pmTransactionManager";
    public static final String PORTFOLIO_ENTITY_PACKAGE = "com.pgim.portfolio.domain.entity.pm";
    public static final String PORTFOLIO_REPOSITORY_PACKAGE = "com.pgim.portfolio.repository.pm";

    // Audit DB Configuration Constants
    public static final String AUDIT_DATASOURCE = "auditDataSource";
    public static final String AUDIT_ENTITY_MANAGER = "auditEntityManagerFactory";
    public static final String AUDIT_TRANSACTION_MANAGER = "auditTransactionManager";
    public static final String AUDIT_ENTITY_PACKAGE = "com.pgim.portfolio.domain.entity.audit";
    public static final String AUDIT_REPOSITORY_PACKAGE = "com.pgim.portfolio.repository.audit";
    public static final String AUDIT_SPRING_DATASOURCE = "spring.datasource.audit";

    // AppUser DB Configuration Constants
    public static final String APPUSER_DATASOURCE = "appUserDataSource";
    public static final String APPUSER_ENTITY_MANAGER = "appUserEntityManagerFactory";
    public static final String APPUSER_TRANSACTION_MANAGER = "appUserTransactionManager";
    public static final String APPUSER_ENTITY_PACKAGE = "com.pgim.portfolio.domain.entity.appuser";
    public static final String APPUSER_REPOSITORY_PACKAGE = "com.pgim.portfolio.repository.appuser";
    public static final String APPUSER_SPRING_DATASOURCE = "spring.datasource.appuser";
}