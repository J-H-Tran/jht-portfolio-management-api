# Implementation Summary: Multi-DataSource Setup in Java/Spring

## Steps Taken

- **Created two MySQL server instances** on different ports and data directories.
- **Initialized each MySQL instance** with a secure password for the root user.
- **Verified connectivity** for both instances using IntelliJ and CLI.
- **Configured Spring Boot** with two datasources in `application.yml` (primary and secondary/audit), using environment variables for credentials.
- **Created separate JPA configurations** for each datasource, with one marked as `@Primary`.
- **Ensured each JPA repository** is scanned in the correct package and linked to the right entity manager.
- **Handled bean naming conflicts** by using unique bean names and `@Qualifier` where needed.
- **Resolved cross-database entity issues** by removing direct JPA relationships between entities in different datasources (e.g., no `@ManyToOne` between audit and trade entities).
- **Refactored service layer** to decouple business logic (trades/portfolios) from audit logic, using a dedicated `TradeAuditService`.
- **Ensured idempotency and immutability** in audit logging by only appending new audit records, never updating or deleting them.
- **Tested application startup** and resolved issues with missing or misconfigured datasource properties.

## Issues Faced & Resolutions

- **MySQL data directory errors**: Fixed by using absolute paths and correct permissions.
- **Access denied for user 'root'**: Resolved by setting the root password and updating Spring config.
- **JPA cross-persistence-unit mapping errors**: Fixed by removing entity relationships across datasources and using IDs instead.
- **Bean definition conflicts**: Resolved by unique bean names and `@Primary` annotation.
- **Missing `EntityManagerFactoryBuilder` bean**: Ensured correct Spring Boot version and configuration.
- **Multiple `EntityManagerFactory` beans**: Marked one as `@Primary` and used `@Qualifier` for the other.
- **Environment variable loading**: Used `.env` file and/or IDE run configuration to inject secrets.
- **Connectivity check failures**: Ensured each MySQL instance was running, accessible, and had the correct credentials.

## Key Takeaways

- Always use environment variables for secrets.
- Never use direct JPA relationships across different datasources.
- Decouple business and audit logic at the service layer.
- Mark one datasource as `@Primary` and use `@Qualifier` for others.
- Test connectivity and configuration for each datasource independently.
- Use defensive coding and ensure idempotency and immutability for audit records.