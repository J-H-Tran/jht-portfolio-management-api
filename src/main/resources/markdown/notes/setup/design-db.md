# Current Setup Architecture

## Overview
The application is designed with a multi-database architecture, where entities, tables, and relationships are distributed across three separate databases: `user_db`, `audit_db`, and `portfolio_db`. Below is a detailed illustration of the setup.

---

## **1. Databases and Entities**

### **1.1 user_db**
- **Entities**: `AppUser`, `AppUserRole`
- **Tables**:
    - `users`: Stores user details.
    - `user_roles`: Maps users to roles (many-to-many relationship).
- **Relationships**:
    - `AppUser` ↔ `AppUserRole`: One-to-Many (via `userRoles` field in `AppUser`).

---

### **1.2 audit_db**
- **Entities**: `TradeAudit`, `AuditDetails`
- **Tables**:
    - `trade_audit`: Stores immutable audit logs for trade operations.
- **Relationships**:
    - `TradeAudit` ↔ `AuditDetails`: Embedded object (JSON column in `trade_audit`).

---

### **1.3 portfolio_db**
- **Entities**: `AuthRole`, `Portfolio`, `Trade`
- **Tables**:
    - `auth_roles`: Stores roles for authorization.
    - `portfolios`: Stores portfolio details.
    - `trades`: Stores trade details.
- **Relationships**:
    - `Portfolio` ↔ `Trade`: One-to-Many (via `portfolio` field in `Trade`).

---

## **2. Entity Relationships**

### **2.1 user_db**
- **`AppUser`**:
    - Fields: `id`, `username`, `email`, `password`, `enabled`, `createdAt`, `updatedAt`, etc.
    - Relationships:
        - One-to-Many with `AppUserRole` (via `userRoles`).
- **`AppUserRole`**:
    - Fields: `id`, `userId`, `roleId`.
    - Relationships:
        - Many-to-One with `AppUser` (via `userId`).

---

### **2.2 audit_db**
- **`TradeAudit`**:
    - Fields: `id`, `tradeId`, `action`, `details`, `createdAt`, `updatedAt`.
    - Relationships:
        - Embedded `AuditDetails` (stored as JSON).
- **`AuditDetails`**:
    - Fields: `note`, `referenceId`.

---

### **2.3 portfolio_db**
- **`AuthRole`**:
    - Fields: `id`, `name`, `description`, `createdAt`.
- **`Portfolio`**:
    - Fields: `id`, `name`, `createdAt`, `updatedAt`.
    - Relationships:
        - One-to-Many with `Trade` (via `trades` field).
- **`Trade`**:
    - Fields: `id`, `portfolioId`, `tradeReferenceId`, `tradeType`, `quantity`, `price`, `status`, `createdAt`, `updatedAt`.
    - Relationships:
        - Many-to-One with `Portfolio` (via `portfolio` field).

---

## **3. Database Relationships**

### **Cross-Database Relationships**
- **`user_db` ↔ `portfolio_db`**:
    - `AppUser` roles (`AppUserRole`) are mapped to `AuthRole` in `portfolio_db` via `roleId`.
- **`portfolio_db` ↔ `audit_db`**:
    - `TradeAudit.tradeId` references `Trade.id` in `portfolio_db`.

---

## **4. Diagram Representation**

```plaintext
user_db:
  AppUser (users) ───< AppUserRole (user_roles)

audit_db:
  TradeAudit (trade_audit) ───< AuditDetails (JSON)

portfolio_db:
  Portfolio (portfolios) ───< Trade (trades)
  AuthRole (auth_roles)

Cross-Database:
  AppUserRole.roleId ───> AuthRole.id
  TradeAudit.tradeId ───> Trade.id