# Database Initialization Guide for Multi-MySQL Setup in Java/Spring

## Overview
This guide covers the steps to set up and initialize more than one MySQL server instance for a multi-database setup in a modern Java/Spring application. It includes best practices for password management, connectivity, and troubleshooting.

---

## 1. Install and Prepare MySQL Instances

- **Install MySQL**: Use Homebrew or your OS package manager to install MySQL.
- **Create Data Directories**: For each instance, create a separate data directory:
  ```sh
  mkdir -p ~/mysql_data1
  mkdir -p ~/mysql_data2
  ```
- **Initialize Data Directories**: Initialize each directory with a password:
  ```sh
  mysqld --initialize --datadir=~/mysql_data1
  mysqld --initialize --datadir=~/mysql_data2
  ```
  > **Note:** Use `--initialize-insecure` only for local dev, never for production.

---

## 2. Start MySQL Instances with Passwords

- **Start each instance on a different port and socket:**
  ```sh
  mysqld --datadir=~/mysql_data1 --port=3306 --socket=~/mysql_data1/mysql.sock --pid-file=~/mysql_data1/mysql.pid --log-error=~/mysql_data1/mysql.log &
  mysqld --datadir=~/mysql_data2 --port=3307 --socket=~/mysql_data2/mysql.sock --pid-file=~/mysql_data2/mysql.pid --log-error=~/mysql_data2/mysql.log &
  ```
- **Set root password for each instance:**
  ```sh
  mysql -u root -S ~/mysql_data1/mysql.sock
  ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_secure_password1';
  mysql -u root -S ~/mysql_data2/mysql.sock
  ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_secure_password2';
  ```

---

## 3. Connectivity Check (IntelliJ/Java)

- **Test connection in IntelliJ:**
    - Use `localhost:3306` and `localhost:3307` with the correct root password for each.
    - Ensure the MySQL server is running and the port/socket is correct.
- **Troubleshooting:**
    - If you see `Access denied for user 'root'@'localhost' (using password: YES)`, ensure the password is set and correct.
    - If you see `Can't create directory`, use absolute paths (no `~`) and ensure permissions are correct.
    - If you see `jdbcUrl is required with driverClassName`, check your Spring datasource config.

---

## 4. Spring Boot Multi-DB Configuration

- **application.yml**:
  ```yaml
  spring:
    datasource:
      pm:
        url: jdbc:mysql://localhost:3306/pm_db
        username: root
        password: ${PM_DB_PASSWORD}
      audit:
        url: jdbc:mysql://localhost:3307/audit_db
        username: root
        password: ${AUDIT_DB_PASSWORD}
  ```
- **Environment Variables**: Store passwords in `.env` or environment variables, not in source code.

---

## 5. Troubleshooting

- **Access Denied**: Double-check user, password, and privileges for each DB.
- **Port Conflicts**: Ensure each MySQL instance uses a unique port.
- **Socket Issues**: Use the correct socket file for CLI connections.
- **Spring Boot Errors**: Ensure each datasource has its own config, and one is marked as `@Primary`.

---

## 6. Security Best Practices

- Never use `--initialize-insecure` in production.
- Always set strong, unique passwords for each MySQL instance.
- Restrict user privileges to only what is needed.
- Do not store passwords in source code; use environment variables or a secrets manager.

---

## 7. References
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Spring Boot DataSource Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties-data)