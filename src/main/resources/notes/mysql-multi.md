# Setting Up Two MySQL Instances on Windows

## Summary
Successfully configured and connected to two MySQL Server instances running simultaneously on Windows, each with different ports and data directories.

## Steps Completed

### 1. Created Second Configuration File
- Created `my2.ini` file at `C:\ProgramData\MySQL\MySQL Server 8.0\my2.ini`
- Key configurations:
    - Port: `3307` (different from default 3306)
    - Data directory: `C:/ProgramData/MySQL/MySQL Server 8.0/Data2`
    - Server ID: `2`
    - Used forward slashes `/` in paths (not backslashes)
    - No quotes around paths in INI file

### 2. Created Data Directory
```powershell
New-Item -Path "C:\ProgramData\MySQL\MySQL Server 8.0\Data2" -ItemType Directory -Force
```

### 3. Initialized Second MySQL Instance
```powershell
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
.\mysqld --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my2.ini" --initialize-insecure --console
```
- **Important**: Used `--defaults-file` (with 's'), not `--default-file`
- Used `--initialize-insecure` flag (creates root user with no password)

### 4. Installed as Windows Service
```powershell
.\mysqld --install MySQL80_Second --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my2.ini"
```

### 5. Started the Second Instance
```powershell
net start MySQL80_Second
```

### 6. Fixed Authentication Issue
**Problem**: Connection failed with "Access denied for user 'root'@'localhost'"
- Root cause: `--initialize-insecure` creates root user with NO password
- IntelliJ was trying to connect WITH a password

**Solution**: Set password for root user on second instance
```powershell
# Connected to second instance
.\mysql -u root -P 3307

# Set password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
EXIT;
```

### 7. Connected in IntelliJ
- **Instance 1**: localhost:3306 (with password)
- **Instance 2**: localhost:3307 (with newly set password)
- Both connections successful ✓

## Final Configuration

### Instance 1 (Original)
- Port: `3306`
- Service Name: `MySQL80`
- Data Directory: `C:/ProgramData/MySQL/MySQL Server 8.0/Data`
- Root Password: Original password

### Instance 2 (New)
- Port: `3307`
- Service Name: `MySQL80_Second`
- Data Directory: `C:/ProgramData/MySQL/MySQL Server 8.0/Data2`
- Root Password: Newly set password

## Managing Both Instances

### Start/Stop Services
```powershell
# Instance 1
net start MySQL80
net stop MySQL80

# Instance 2
net start MySQL80_Second
net stop MySQL80_Second
```

### Connect via Command Line
```powershell
# Instance 1
mysql -u root -p -P 3306

# Instance 2
mysql -u root -p -P 3307
```

## Key Lessons Learned
- Always use `--defaults-file` (plural) not `--default-file`
- `--initialize-insecure` creates root with NO password
- Each instance needs unique: port, data directory, service name, server ID
- Use forward slashes in INI file paths
- Don't quote paths in INI files


Here are some typical real-world use cases for the operations that can be performed with the `portfolios`, `trades`, and `trade_audit` tables across two databases:
### Portfolio Operations (DB1)
1. **Create Portfolio**: Users can create a new portfolio to manage their investments.
2. **Edit Portfolio**: Users can update portfolio details such as name, description, or associated metadata.
3. **View Portfolio**: Users can fetch details of a specific portfolio or list all portfolios.
4. **Delete Portfolio**: Users can delete a portfolio if it is no longer needed.

### Trade Operations (DB1)
1. **Submit Trade**: Users can submit a new trade for a specific portfolio, specifying details like trade type (BUY/SELL), quantity, and price.
2. **Edit Trade**: Users can update trade details (e.g., price, quantity) before the trade is finalized.
3. **View Trades**: Users can view all trades for a specific portfolio or fetch details of a specific trade.
4. **Delete Trade**: Users can delete a trade if it was created in error or is no longer valid.

### Trade Audit Operations (DB2)
1. **Log Trade Events**: Automatically log actions like trade submission, updates, or deletions for auditing purposes.
2. **View Audit Logs**: Administrators or auditors can view the history of actions performed on trades for compliance and troubleshooting.
3. **Filter Audit Logs**: Retrieve audit logs for specific trades or actions (e.g., all `SUBMIT` actions for a given time period).
4. **Analyze Audit Data**: Use audit logs to generate reports or insights, such as identifying frequent updates or cancellations.

### Cross-Functional Use Cases
1. **Portfolio Performance Analysis**: Combine portfolio and trade data to calculate performance metrics like ROI or portfolio diversification.
2. **Compliance Monitoring**: Use audit logs to ensure trades comply with regulatory requirements.
3. **Fraud Detection**: Analyze audit logs for unusual patterns, such as frequent trade cancellations or updates.
4. **User Notifications**: Notify users of significant events, such as successful trade submissions or portfolio updates.
5. **Data Integrity Checks**: Ensure that all trades in the `trades` table have corresponding audit logs in the `trade_audit` table.

These use cases demonstrate how the system can support both user-driven operations and backend processes for auditing, compliance, and analytics.