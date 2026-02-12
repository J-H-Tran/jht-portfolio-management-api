# Multi-Datasource Setup Guide for Spring Boot 3.x (Production-Ready)

## Table of Contents
1. [Overview](#overview)
2. [Architecture & Use Cases](#architecture--use-cases)
3. [Dependencies](#dependencies)
4. [Core Components](#core-components)
5. [Configuration](#configuration)
6. [Transaction Management](#transaction-management)
7. [Data Integrity & Idempotency](#data-integrity--idempotency)
8. [Security Best Practices](#security-best-practices)
9. [Design Considerations & Tradeoffs](#design-considerations--tradeoffs)
10. [Production Checklist](#production-checklist)
11. [Troubleshooting](#troubleshooting)

---

## Overview

Multi-datasource configuration allows a single Spring Boot application to connect to multiple databases simultaneously. This is essential for:

- **Separation of Concerns**: Isolating transactional data from audit logs, reporting, or analytics.
- **Microservices Integration**: Connecting to legacy systems or external databases.
- **Read/Write Splitting**: Optimizing performance with separate read replicas.
- **Compliance**: Meeting regulatory requirements for data segregation.

**Key Concepts:**
- Each datasource requires its own `DataSource`, `EntityManagerFactory`, and `PlatformTransactionManager`.
- Repositories and entities must be organized into separate packages per datasource.
- Cross-datasource JPA relationships are **not supported**; use service-layer logic instead.
- Transactions are isolated per datasource; distributed transactions require additional setup.

---

## Architecture & Use Cases

### Common Multi-Datasource Patterns

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Spring Boot Application                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌───────────────────────────┐    ┌───────────────────────────┐   │
│  │   Primary DataSource      │    │  Secondary DataSource     │   │
│  │   (Business Data)         │    │  (Audit Logs)             │   │
│  ├───────────────────────────┤    ├───────────────────────────┤   │
│  │ • Portfolio Management    │    │ • Trade Audit Logs        │   │
│  │ • Trade Execution         │    │ • User Activity Logs      │   │
│  │ • User Management         │    │ • Compliance Records      │   │
│  └───────────────────────────┘    └───────────────────────────┘   │
│           │                                   │                    │
│           ▼                                   ▼                    │
│  ┌───────────────────────────┐    ┌───────────────────────────┐   │
│  │ MySQL Instance (Port 3306)│    │ MySQL Instance (Port 3307)│   │
│  │ Database: portfolio_db    │    │ Database: audit_db        │   │
│  └───────────────────────────┘    └───────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### Use Cases
1. **Audit Trail Separation**: Keep immutable audit logs in a separate database for compliance.
2. **Multi-Tenancy**: Each tenant has their own database.
3. **Legacy Integration**: Connect to existing databases while building new systems.
4. **Performance Optimization**: Separate read-heavy queries from write-heavy operations.
5. **Data Sovereignty**: Store data in different regions/databases to comply with regulations.

---

## Dependencies

Add these to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Spring Boot Web (for REST APIs) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- HikariCP (Connection Pooling) -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
    
    <!-- Lombok (Optional, for reducing boilerplate) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- MapStruct (for DTO mapping) -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
</dependencies>
```

---

## Core Components

### 1. Application Configuration (`application.yml`)

```yaml
spring:
  application:
    name: portfolio-api
    
  # Primary DataSource (Portfolio Management)
  datasource:
    url: jdbc:mysql://localhost:3306/portfolio_db
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      
  # Secondary DataSource (Audit Logs)
  datasource:
    audit:
      url: jdbc:mysql://localhost:3307/audit_db
      username: ${DB_AUDIT_USERNAME:root}
      password: ${DB_AUDIT_PASSWORD:root}
      driver-class-name: com.mysql.cj.jdbc.Driver
      hikari:
        maximum-pool-size: 5
        minimum-idle: 2
        connection-timeout: 30000
        
  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: validate  # Use 'validate' in production
    show-sql: false       # Disable in production
    open-in-view: false   # CRITICAL: Must be false for multi-datasource
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
        use_sql_comments: true
        
server:
  port: 8080

logging:
  level:
    org.springframework.orm.jpa: INFO
    org.hibernate.SQL: DEBUG
    com.pgim.portfolio: DEBUG
```

**Key Configuration Points:**
- `spring.jpa.open-in-view: false` - **MANDATORY** for multi-datasource. Spring Boot's default open-in-view expects a single `EntityManagerFactory`.
- Use environment variables for sensitive credentials (`${DB_PASSWORD}`).
- Configure HikariCP pool sizes based on your workload (primary DB typically needs more connections).

---

### 2. Primary DataSource Configuration

```java
@Configuration
@EnableJpaRepositories(
    basePackages = "com.pgim.portfolio.repository.pm",
    entityManagerFactoryRef = "pmEntityManagerFactory",
    transactionManagerRef = "pmTransactionManager"
)
public class PmDataSourceConfig {
    
    /**
     * Primary DataSource for business data (portfolios, trades).
     * Marked as @Primary to be used as default when no qualifier is specified.
     */
    @Bean(name = "pmDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource pmDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    /**
     * Primary EntityManagerFactory.
     * Scans entity packages for the primary datasource.
     */
    @Bean(name = "pmEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean pmEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("pmDataSource") DataSource dataSource
    ) {
        return builder
            .dataSource(dataSource)
            .packages("com.pgim.portfolio.domain.entity.pm")
            .persistenceUnit("pm")
            .properties(hibernateProperties())
            .build();
    }
    
    /**
     * Primary Transaction Manager.
     * Handles transactions for the primary datasource.
     */
    @Bean(name = "pmTransactionManager")
    @Primary
    public PlatformTransactionManager pmTransactionManager(
            @Qualifier("pmEntityManagerFactory") EntityManagerFactory pmEntityManagerFactory
    ) {
        return new JpaTransactionManager(pmEntityManagerFactory);
    }
    
    /**
     * Hibernate-specific properties for fine-tuning performance.
     */
    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.jdbc.batch_size", 20);
        properties.put("hibernate.order_inserts", true);
        properties.put("hibernate.order_updates", true);
        return properties;
    }
}
```

---

### 3. Secondary DataSource Configuration

```java
@Configuration
@EnableJpaRepositories(
    basePackages = "com.pgim.portfolio.repository.audit",
    entityManagerFactoryRef = "auditEntityManagerFactory",
    transactionManagerRef = "auditTransactionManager"
)
public class AuditDataSourceConfig {
    
    /**
     * Secondary DataSource for audit logs.
     * Not marked as @Primary.
     */
    @Bean(name = "auditDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.audit")
    public DataSource auditDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    /**
     * Secondary EntityManagerFactory.
     * Scans entity packages for the audit datasource.
     */
    @Bean(name = "auditEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean auditEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("auditDataSource") DataSource dataSource
    ) {
        return builder
            .dataSource(dataSource)
            .packages("com.pgim.portfolio.domain.entity.audit")
            .persistenceUnit("audit")
            .build();
    }
    
    /**
     * Secondary Transaction Manager.
     * Handles transactions for the audit datasource.
     */
    @Bean(name = "auditTransactionManager")
    public PlatformTransactionManager auditTransactionManager(
            @Qualifier("auditEntityManagerFactory") EntityManagerFactory auditEntityManagerFactory
    ) {
        return new JpaTransactionManager(auditEntityManagerFactory);
    }
}
```

**Critical Rules:**
1. **Separate Packages**: Each datasource must scan different repository and entity packages.
2. **Unique Bean Names**: All beans must have unique names (`pmDataSource`, `auditDataSource`).
3. **@Primary Annotation**: Mark one datasource as primary to avoid ambiguity.
4. **@Qualifier Annotation**: Use qualifiers when injecting non-primary beans.

---

### 4. Entity Design (Primary Database)

```java
@Entity
@Table(name = "trades")
@Data
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    
    @Column(name = "trade_reference_id", nullable = false, unique = true)
    private String tradeReferenceId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false)
    private TradeType tradeType;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status = TradeStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum TradeStatus {
        PENDING, VALIDATED, FAILED, COMPLETED, CANCELLED
    }
    
    public enum TradeType {
        BUY, SELL
    }
}
```

---

### 5. Entity Design (Secondary Database - Audit)

```java
@Entity
@Table(name = "trade_audit")
@Data
public class TradeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * CRITICAL: Store tradeId as a simple Long, NOT as a JPA relationship.
     * Cross-datasource JPA relationships are not supported.
     */
    @Column(name = "trade_id", nullable = false)
    private Long tradeId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;
    
    @Column(nullable = false, columnDefinition = "JSON")
    private String details;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum AuditAction {
        CREATE, ADJUST, CANCEL
    }
}
```

**Why No JPA Relationship?**
- JPA/Hibernate cannot manage relationships across different `EntityManagerFactory` instances.
- Attempting to use `@ManyToOne` or `@OneToMany` across datasources will cause:
  ```
  org.hibernate.AnnotationException: Association 'TradeAudit.trade' targets 
  the type 'Trade' which does not belong to the same persistence unit
  ```
- **Solution**: Use service-layer logic to enforce referential integrity.

---

## Transaction Management

### 1. Isolated Transactions Per Datasource

```java
@Service
public class TradeServiceImpl implements TradeService {
    private final TradeRepository tradeRepository;
    private final TradeAuditService tradeAuditService;
    
    /**
     * Save trade in primary DB with primary transaction manager.
     * Then log audit in secondary DB with secondary transaction manager.
     */
    @Transactional("pmTransactionManager")
    public TradeDTO addTrade(TradeDTO tradeDTO) {
        // Save to primary DB
        Trade trade = tradeMapper.toEntity(tradeDTO);
        Trade savedTrade = tradeRepository.save(trade);
        
        // Audit logging in secondary DB (separate transaction)
        tradeAuditService.logTradeEvent(savedTrade.getId(), "CREATE", "Trade created");
        
        return tradeMapper.toDTO(savedTrade);
    }
}

@Service
public class TradeAuditServiceImpl implements TradeAuditService {
    private final AuditRepository auditRepository;
    private final TradeRepository tradeRepository; // Primary DB repository
    
    /**
     * Log audit event with secondary transaction manager.
     * Enforce referential integrity at service layer.
     */
    @Transactional("auditTransactionManager")
    public void logTradeEvent(Long tradeId, String action, String details) {
        // Defensive: Ensure trade exists in primary DB
        if (tradeRepository.findById(tradeId).isEmpty()) {
            throw new IllegalArgumentException("Trade not found: " + tradeId);
        }
        
        // Immutable: Create new audit record
        TradeAudit audit = new TradeAudit();
        audit.setTradeId(tradeId);
        audit.setAction(AuditAction.valueOf(action));
        audit.setDetails(details);
        
        auditRepository.save(audit);
    }
}
```

**Transaction Best Practices:**
- Always specify the transaction manager: `@Transactional("pmTransactionManager")`.
- Keep transactions short and focused on a single datasource.
- Avoid distributed transactions unless absolutely necessary (complex, performance overhead).

---

### 2. Handling Failures Across Datasources

```java
@Service
public class TradeServiceImpl implements TradeService {
    
    @Transactional("pmTransactionManager")
    public TradeDTO submitTrade(TradeDTO tradeDTO) {
        // 1. Save trade in primary DB
        Trade savedTrade = tradeRepository.save(tradeMapper.toEntity(tradeDTO));
        
        try {
            // 2. Log audit in secondary DB (separate transaction)
            tradeAuditService.logTradeEvent(savedTrade.getId(), "SUBMIT", "Trade submitted");
        } catch (Exception e) {
            // Log error but don't rollback main transaction
            logger.error("Failed to log audit for trade {}: {}", savedTrade.getId(), e.getMessage());
            // Optional: Store failed audit attempt for retry
        }
        
        return tradeMapper.toDTO(savedTrade);
    }
}
```

**Failure Handling Strategies:**
1. **Log and Continue**: Main transaction succeeds, audit failure is logged.
2. **Compensating Transaction**: Manually rollback changes if audit fails (complex).
3. **Message Queue**: Publish audit events to a queue for async processing (resilient).
4. **Saga Pattern**: Coordinate multi-step operations across datasources.

---

## Data Integrity & Idempotency

### 1. Enforcing Referential Integrity

Since JPA relationships don't work across datasources, enforce integrity in your service layer:

```java
@Service
public class TradeAuditServiceImpl implements TradeAuditService {
    
    @Transactional("auditTransactionManager")
    public void logTradeEvent(Long tradeId, String action, String details) {
        // DEFENSIVE: Validate that the referenced trade exists
        Trade trade = tradeRepository.findById(tradeId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot audit non-existent trade: " + tradeId));
        
        // IDEMPOTENCY: Check for duplicate audit entries
        boolean exists = auditRepository.existsByTradeIdAndActionAndCreatedAtAfter(
            tradeId, 
            AuditAction.valueOf(action), 
            LocalDateTime.now().minusMinutes(5)
        );
        
        if (exists) {
            logger.warn("Duplicate audit log detected for trade {} and action {}", tradeId, action);
            return; // Skip duplicate
        }
        
        // Create immutable audit record
        TradeAudit audit = new TradeAudit();
        audit.setTradeId(tradeId);
        audit.setAction(AuditAction.valueOf(action));
        audit.setDetails(details);
        auditRepository.save(audit);
    }
}
```

---

### 2. Custom SQL Queries for Cross-Database Operations

```java
@Repository
public interface AuditRepository extends JpaRepository<TradeAudit, Long> {
    
    /**
     * Find all audit logs for a specific trade.
     * Note: This is a simple query within the audit DB only.
     */
    List<TradeAudit> findByTradeId(Long tradeId);
    
    /**
     * Check for recent duplicate audit entries (idempotency).
     */
    @Query("SELECT COUNT(a) > 0 FROM TradeAudit a WHERE a.tradeId = :tradeId " +
           "AND a.action = :action AND a.createdAt > :after")
    boolean existsByTradeIdAndActionAndCreatedAtAfter(
        @Param("tradeId") Long tradeId, 
        @Param("action") AuditAction action, 
        @Param("after") LocalDateTime after
    );
}
```

**For Cross-Database Joins:**
- Avoid SQL joins across databases; fetch data separately and join in application code.
- Use caching (Redis) for frequently accessed cross-database data.
- Consider a materialized view or denormalized table if performance is critical.

---

### 3. Service Layer Join Pattern

```java
@Service
public class TradeReportService {
    private final TradeRepository tradeRepository;       // Primary DB
    private final AuditRepository auditRepository;       // Secondary DB
    
    /**
     * Get trade with its audit history (cross-database).
     * Fetches from both DBs and joins in application code.
     */
    public TradeWithAuditDTO getTradeWithAuditHistory(Long tradeId) {
        // 1. Fetch trade from primary DB
        Trade trade = tradeRepository.findById(tradeId)
            .orElseThrow(() -> new EntityNotFoundException("Trade not found"));
        
        // 2. Fetch audit logs from secondary DB
        List<TradeAudit> auditLogs = auditRepository.findByTradeId(tradeId);
        
        // 3. Join in application code
        TradeWithAuditDTO dto = new TradeWithAuditDTO();
        dto.setTrade(tradeMapper.toDTO(trade));
        dto.setAuditHistory(auditLogs.stream()
            .map(auditMapper::toDTO)
            .collect(Collectors.toList()));
        
        return dto;
    }
}
```

---

## Security Best Practices

### 1. Credential Management

```yaml
# Use environment variables for sensitive data
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/portfolio_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  datasource:
    audit:
      url: jdbc:mysql://localhost:3307/audit_db
      username: ${DB_AUDIT_USERNAME}
      password: ${DB_AUDIT_PASSWORD}
```

**Production Recommendations:**
- Use secret management tools (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault).
- Rotate credentials regularly.
- Use different credentials for each datasource.
- Never commit credentials to version control.

---

### 2. Database User Permissions

```sql
-- Primary DB: Full read/write for application
CREATE USER 'app_user'@'%' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON portfolio_db.* TO 'app_user'@'%';

-- Audit DB: Write-only (append-only for immutability)
CREATE USER 'audit_user'@'%' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT ON audit_db.* TO 'audit_user'@'%';
-- No UPDATE or DELETE to ensure audit logs are immutable
```

---

### 3. Connection Pool Security

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10          # Limit connections
      connection-timeout: 30000      # 30 seconds
      idle-timeout: 600000           # 10 minutes
      max-lifetime: 1800000          # 30 minutes (rotate connections)
      leak-detection-threshold: 60000 # Detect connection leaks
```

---

### 4. SQL Injection Prevention

```java
// ALWAYS use parameterized queries
@Query("SELECT t FROM Trade t WHERE t.tradeReferenceId = :refId")
Trade findByRefId(@Param("refId") String refId);

// NEVER concatenate user input into SQL
// BAD: "SELECT * FROM trades WHERE id = " + userInput
```

---

## Design Considerations & Tradeoffs

### 1. Complexity vs. Benefits

| Aspect | Single Datasource | Multi-Datasource |
|--------|-------------------|------------------|
| Configuration | Simple | Complex (multiple beans, packages) |
| Transaction Management | Straightforward | Requires explicit transaction managers |
| Cross-Table Joins | Easy (JPA relationships) | Must join in application code |
| Data Consistency | ACID guarantees | Eventual consistency across DBs |
| Performance | Good | Better (can optimize per workload) |
| Scalability | Limited | High (scale each DB independently) |
| Debugging | Easy | Harder (trace across multiple DBs) |
| Testing | Simple | Requires multiple test databases |

---

### 2. When to Use Multi-Datasource

**Use Multi-Datasource When:**
- ✅ You need to separate audit logs for compliance.
- ✅ You're integrating with legacy systems.
- ✅ You want to optimize read/write performance separately.
- ✅ You need data sovereignty (different regions/databases).
- ✅ You're implementing multi-tenancy with database-per-tenant.

**Avoid Multi-Datasource When:**
- ❌ Your application is simple and doesn't need separation.
- ❌ You need frequent cross-database joins (performance hit).
- ❌ You need distributed transactions (XA protocol is complex).
- ❌ Your team lacks experience with multi-datasource patterns.

---

### 3. Distributed Transactions (XA Protocol)

For ACID transactions across multiple databases, you need a distributed transaction manager (JTA):

```xml
<!-- Add Atomikos or Bitronix for distributed transactions -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```

**Tradeoffs:**
- **Pros**: Strong consistency, ACID guarantees across DBs.
- **Cons**: Complex setup, performance overhead, potential deadlocks.
- **Recommendation**: Avoid unless absolutely necessary; use saga pattern instead.

---

### 4. Alternative: Event-Driven Architecture

Instead of multi-datasource, consider:

```java
@Service
public class TradeServiceImpl implements TradeService {
    
    @Transactional
    public TradeDTO addTrade(TradeDTO tradeDTO) {
        Trade savedTrade = tradeRepository.save(tradeMapper.toEntity(tradeDTO));
        
        // Publish event to message queue (Kafka, RabbitMQ)
        eventPublisher.publish(new TradeCreatedEvent(savedTrade.getId()));
        
        return tradeMapper.toDTO(savedTrade);
    }
}

@Component
public class AuditEventListener {
    
    @EventListener
    public void onTradeCreated(TradeCreatedEvent event) {
        // Async audit logging in separate transaction/DB
        auditService.logTradeEvent(event.getTradeId(), "CREATE", "Trade created");
    }
}
```

**Benefits:**
- Loose coupling between services.
- Async processing (better performance).
- Resilient to failures (retry logic).

---

## Production Checklist

### 1. Configuration Checklist

- ✅ Each datasource has unique bean names (`pmDataSource`, `auditDataSource`).
- ✅ One datasource is marked as `@Primary`.
- ✅ Repository packages are separated (`repository.pm`, `repository.audit`).
- ✅ Entity packages are separated (`entity.pm`, `entity.audit`).
- ✅ `spring.jpa.open-in-view` is set to `false`.
- ✅ Credentials are stored in environment variables or secret managers.
- ✅ Connection pool sizes are tuned for your workload.
- ✅ Transaction managers are explicitly specified in `@Transactional`.

---

### 2. Entity Design Checklist

- ✅ No JPA relationships between entities in different datasources.
- ✅ Referential integrity is enforced in service layer.
- ✅ Audit entities are immutable (no UPDATE/DELETE operations).
- ✅ All entities have proper indexes on foreign key columns.
- ✅ Timestamp fields use `@CreationTimestamp` and `@UpdateTimestamp`.

---

### 3. Service Layer Checklist

- ✅ Each service method specifies the correct transaction manager.
- ✅ Cross-database data fetching is done in application code, not SQL joins.
- ✅ Idempotency checks are in place for critical operations.
- ✅ Defensive validation ensures referenced entities exist.
- ✅ Error handling logs failures but doesn't block main flow.
- ✅ Audit logging is decoupled from business logic.

---

### 4. Testing Checklist

- ✅ Integration tests run against both databases.
- ✅ Test failure scenarios (audit DB down, primary DB down).
- ✅ Test concurrent transactions for race conditions.
- ✅ Test idempotency (retry logic doesn't create duplicates).
- ✅ Load testing to validate connection pool sizes.

---

### 5. Monitoring & Observability

```yaml
# Enable metrics for both datasources
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  metrics:
    enable:
      hikari: true
```

**Monitor:**
- Connection pool usage (active, idle, waiting).
- Query performance (slow query logs).
- Transaction success/failure rates.
- Audit log completeness (compare with primary DB changes).

---

## Troubleshooting

### 1. Bean Definition Conflicts

**Error:**
```
The bean 'portfolioRepository' could not be registered. 
A bean with that name has already been defined...
```

**Solution:**
- Ensure repository packages don't overlap.
- Remove `@EnableJpaRepositories` from main application class if you have dedicated configs.

---

### 2. EntityManagerFactory Not Found

**Error:**
```
No qualifying bean of type 'EntityManagerFactory' available
```

**Solution:**
- Mark one `EntityManagerFactory` as `@Primary`.
- Or use `@Qualifier` when injecting:
  ```java
  @Autowired
  @Qualifier("pmEntityManagerFactory")
  private EntityManagerFactory emf;
  ```

---

### 3. Cross-Datasource JPA Relationship Error

**Error:**
```
org.hibernate.AnnotationException: Association 'TradeAudit.trade' targets 
the type 'Trade' which does not belong to the same persistence unit
```

**Solution:**
- Remove JPA relationships (`@ManyToOne`, `@OneToMany`) between entities in different datasources.
- Use `Long tradeId` instead of `Trade trade` in the audit entity.

---

### 4. HikariCP jdbcUrl Error

**Error:**
```
java.lang.IllegalArgumentException: jdbcUrl is required with driverClassName
```

**Solution:**
- Check that property prefix matches config: `spring.datasource.audit`.
- Use `url` in YAML, not `jdbcUrl` (Spring Boot maps it automatically).
- Ensure environment variables are set for credentials.

---

### 5. Open-in-View Error

**Error:**
```
expected single matching bean but found 2: 
auditEntityManagerFactory, pmEntityManagerFactory
```

**Solution:**
- Set `spring.jpa.open-in-view: false` in `application.yml`.

---

## Real-World Example: Portfolio Management System

### Project Structure

```
src/main/java/com/pgim/portfolio/
├── domain/
│   ├── entity/
│   │   ├── pm/                 # Primary DB entities
│   │   │   ├── Portfolio.java
│   │   │   └── Trade.java
│   │   └── audit/              # Secondary DB entities
│   │       └── TradeAudit.java
│   └── dto/
│       ├── pm/
│       │   ├── PortfolioDTO.java
│       │   └── TradeDTO.java
│       └── audit/
│           └── TradeAuditDTO.java
├── repository/
│   ├── pm/                     # Primary DB repositories
│   │   ├── PortfolioRepository.java
│   │   └── TradeRepository.java
│   └── audit/                  # Secondary DB repositories
│       └── AuditRepository.java
├── service/
│   ├── pm/                     # Primary DB services
│   │   ├── PortfolioService.java
│   │   └── TradeService.java
│   └── audit/                  # Secondary DB services
│       └── TradeAuditService.java
├── api/
│   ├── config/                 # Datasource configurations
│   │   ├── PmDataSourceConfig.java
│   │   └── AuditDataSourceConfig.java
│   └── controller/
│       ├── PortfolioController.java
│       └── TradeController.java
└── MainApiApplication.java
```

---

## Summary

Multi-datasource setup in Spring Boot requires careful planning and configuration, but it provides significant benefits for:
- **Compliance**: Separate audit logs for regulatory requirements.
- **Performance**: Optimize each database for its workload.
- **Scalability**: Scale databases independently.
- **Maintainability**: Clear separation of concerns.

**Key Takeaways:**
1. Always mark one datasource as `@Primary`.
2. Never use JPA relationships across datasources.
3. Enforce referential integrity in the service layer.
4. Use explicit transaction managers (`@Transactional("pmTransactionManager")`).
5. Set `spring.jpa.open-in-view: false`.
6. Store credentials in environment variables or secret managers.
7. Monitor connection pools and transaction performance.
8. Test thoroughly, especially failure scenarios.

For production systems, follow the checklists and consider event-driven architecture for complex cross-database operations.

---

**Next Steps:**
- Implement connection pool monitoring and alerts.
- Add retry logic for audit logging failures.
- Consider caching (Redis) for frequently accessed cross-database data.
- Implement circuit breakers for database health checks.
- Add comprehensive logging for transaction boundaries.