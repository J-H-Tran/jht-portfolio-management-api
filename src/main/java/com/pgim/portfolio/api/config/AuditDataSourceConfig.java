package com.pgim.portfolio.api.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static com.pgim.portfolio.api.constant.CommonConstants.AUDIT_DATASOURCE;
import static com.pgim.portfolio.api.constant.CommonConstants.AUDIT_ENTITY_MANAGER_FACTORY;
import static com.pgim.portfolio.api.constant.CommonConstants.AUDIT_ENTITY_PACKAGE;
import static com.pgim.portfolio.api.constant.CommonConstants.AUDIT_REPOSITORY_PACKAGE;
import static com.pgim.portfolio.api.constant.CommonConstants.AUDIT_SPRING_DATASOURCE;
import static com.pgim.portfolio.api.constant.CommonConstants.AUDIT_TRANSACTION_MANAGER;
import static com.pgim.portfolio.api.util.SqlScriptExecutor.executeSqlScript;

/**
 * SecondaryDataSourceConfig demonstrates how to configure a second database connection in Spring Boot.
 * This is useful for scenarios like audit logging, reporting, or integrating with legacy systems.
 *
 * Step-by-step walkthrough:
 * 1. Add secondary datasource properties in application.yml under spring.datasource.secondary.
 * 2. Define a DataSource bean for the secondary DB using @ConfigurationProperties.
 * 3. Create an EntityManagerFactory bean for the secondary DB, specifying the entity package.
 * 4. Create a TransactionManager bean for the secondary DB.
 * 5. Use @EnableJpaRepositories to point to the repository package for the secondary DB.
 * 6. Use @EntityScan to specify the entity package for the secondary DB.
 *
 * Each bean is named uniquely to avoid conflicts with the primary datasource beans.
 * This config allows you to use two separate databases in one Spring Boot application.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = AUDIT_REPOSITORY_PACKAGE, // package for secondary repos
        entityManagerFactoryRef = AUDIT_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = AUDIT_TRANSACTION_MANAGER
)
@EntityScan(basePackages = AUDIT_ENTITY_PACKAGE)
public class AuditDataSourceConfig {


    /**
     * Defines the secondary DataSource bean.
     * Properties are loaded from application.yml (spring.audit-datasource).
     */
    @Bean(name = AUDIT_DATASOURCE)
    @ConfigurationProperties(prefix = AUDIT_SPRING_DATASOURCE)
    public DataSource auditDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Defines the EntityManagerFactory for the secondary DB.
     * Points to the entity package for audit entities.
     */
    @Bean(name = AUDIT_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean auditEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(AUDIT_DATASOURCE) DataSource auditDataSource
    ) {
        return builder
                .dataSource(auditDataSource)
                .packages(AUDIT_ENTITY_PACKAGE)
                .persistenceUnit("audit")
                .build();
    }

    /**
     * Defines the TransactionManager for the secondary DB.
     * Ensures transactions are managed separately from the primary DB.
     */
    @Bean(name = AUDIT_TRANSACTION_MANAGER)
    public PlatformTransactionManager auditTransactionManager(
            @Qualifier(AUDIT_ENTITY_MANAGER_FACTORY)
            EntityManagerFactory auditEntityManagerFactory
    ) {
        return new JpaTransactionManager(auditEntityManagerFactory);
    }

    @Bean
    public ApplicationRunner auditDbInitializer(
            @Qualifier(AUDIT_DATASOURCE) DataSource auditDataSource
    ) {
        return args -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(auditDataSource);
            executeSqlScript(jdbcTemplate, "schema-audit.sql");
            executeSqlScript(jdbcTemplate, "data-audit.sql");
        };
    }
}
