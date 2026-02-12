# Guide: Setting Up Multiple MySQL Server Instances for Multi-DB Java/Spring Applications

## Overview
This guide explains how to set up more than one MySQL server instance on a single machine for use in a multi-database Java/Spring Boot project. It covers installation, configuration, password setup, and troubleshooting, with a focus on requirements for connectivity checks in IntelliJ and production security best practices.

---

## 1. Why Multiple MySQL Servers?
- **Isolation:** Separate business and audit databases for compliance, performance, or reliability.
- **Portability:** Run different versions or configurations for different modules.
- **Testing:** Simulate production-like environments locally.

---

## 2. Installation & Initialization

### Install MySQL via Homebrew (macOS)
```zsh
brew install mysql
```

### Install a Second MySQL Version (if needed)
```zsh
brew install mysql@8.4
```

### Create Separate Data Directories
```zsh
mkdir -p ~/mysql_data1
mkdir -p ~/mysql_data2
```

### Initialize Each Data Directory
```zsh
mysqld --initialize-insecure --datadir=~/mysql_data1
mysqld --initialize-insecure --datadir=~/mysql_data2
```
- `--initialize-insecure` creates the DB without a root password (you must set one later).

---

## 3. Start Each MySQL Server on a Different Port

### Start First Server (default port 3306)
```zsh
mysqld --no-defaults --datadir=~/mysql_data1 --port=3306 --socket=~/mysql_data1/mysql.sock --pid-file=~/mysql_data1/mysql.pid --log-error=~/mysql_data1/mysql.log &
```

### Start Second Server (custom port 3307)
```zsh
mysqld --no-defaults --datadir=~/mysql_data2 --port=3307 --socket=~/mysql_data2/mysql.sock --pid-file=~/mysql_data2/mysql.pid --log-error=~/mysql_data2/mysql.log &
```

---

## 4. Set/Initialize Root Password (Critical for Connectivity)
By default, `--initialize-insecure` leaves root password blank. IntelliJ and Spring Boot require a password for connectivity.

### Connect to Each Server
```zsh
mysql -u root -h 127.0.0.1 -P 3306
mysql -u root -h 127.0.0.1 -P 3307
```

### Set Root Password
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_secure_password';
FLUSH PRIVILEGES;
```
- Repeat for each server/port.
- Use strong, unique passwords for each instance.

---

## 5. Create Databases for Your Application
```sql
CREATE DATABASE portfolio_db;
CREATE DATABASE audit_db;
```
- Run on the appropriate server (e.g., portfolio_db on 3306, audit_db on 3307).

---

## 6. Grant User Privileges
For production, avoid using root. Create dedicated users:

```sql
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'app_password';
GRANT ALL PRIVILEGES ON portfolio_db.* TO 'app_user'@'localhost';
GRANT ALL PRIVILEGES ON audit_db.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## 7. Connectivity Check in IntelliJ
- Add each MySQL server as a separate datasource in IntelliJ.
- Use the correct port, username, and password.
- Test connection for each.
- If password is missing, IntelliJ will fail the connectivity check.

---

## 8. Spring Boot Configuration Example
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/portfolio_db
    username: app_user
    password: app_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  datasource:
    audit:
      url: jdbc:mysql://localhost:3307/audit_db
      username: app_user
      password: app_password
      driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## 9. Troubleshooting

### Common Issues & Solutions

- **Cannot connect: Access denied for user 'root'@'localhost' (using password: YES)**
    - Solution: Set root password using `ALTER USER` as above.

- **Socket file not found (`/tmp/mysql.sock`)**
    - Solution: Specify `--socket` option when starting server. Use `-h 127.0.0.1` instead of `localhost` when connecting.

- **Port conflict**
    - Solution: Ensure each server uses a unique port (e.g., 3306, 3307).

- **Permission denied or directory error**
    - Solution: Ensure data directories exist and are writable by the user running `mysqld`.

- **Connectivity check fails in IntelliJ**
    - Solution: Confirm port, username, password, and database name. Test with `mysql` CLI first.

- **Password not set after initialization**
    - Solution: Use `ALTER USER` to set password before connecting via IntelliJ or Spring Boot.

- **Multiple MySQL versions**
    - Solution: Use Homebrew to manage versions. Start each with explicit paths and ports.

---

## 10. Security Best Practices
- Use strong, unique passwords for each server/user.
- Never use root in production; create dedicated users.
- Restrict user privileges to only necessary databases.
- Rotate passwords regularly.
- Use environment variables or secret managers for credentials in Spring Boot.

---

## 11. Stopping Servers
```zsh
mysqladmin -u root -p shutdown -h 127.0.0.1 -P 3306
mysqladmin -u root -p shutdown -h 127.0.0.1 -P 3307
```

---

## 12. Summary
- Initialize each MySQL server with its own data directory and port.
- Set root password immediately after initialization.
- Create databases and users for your application.
- Configure IntelliJ and Spring Boot with correct credentials.
- Follow security best practices for production.
- Troubleshoot connectivity and permission issues as needed.