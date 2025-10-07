# Fraud Detection Module - Implementation Summary

## Overview
The Fraud Detection Module automatically analyzes all card operations and generates alerts based on suspicious patterns.

## Components Implemented

### 1. Entity Layer

#### AlertLevel.java (Enum)
- **INFO**: Minor anomaly detected (new location)
- **WARNING**: Suspicious pattern detected (high amount, unusual activity)
- **CRITICAL**: High risk fraud detected (rapid transactions)

#### FraudAlert.java (Record)
```java
public record FraudAlert(
    int id,
    int operationId,
    AlertLevel level,
    String reason,
    LocalDateTime detectedAt
)
```

### 2. DAO Layer

#### FraudAlertDAO.java (Interface)
Methods:
- `save(FraudAlert)` - Save new alert
- `findById(int)` - Find alert by ID
- `findAll()` - Get all alerts
- `findByOperationId(int)` - Find alerts for specific operation
- `findByLevel(AlertLevel)` - Filter by alert level
- `findByDateRange(start, end)` - Find alerts in date range
- `findRecent(limit)` - Get most recent alerts

#### FraudAlertDAOImp.java
Full JDBC implementation with PreparedStatements for all operations.

### 3. Service Layer

#### FraudService.java
**Automatic Fraud Detection Rules:**

1. **High Amount Detection**
   - Threshold: $5,000
   - Level: WARNING
   - Triggers on any single transaction over threshold

2. **Rapid Transactions**
   - Pattern: 3+ transactions in 5 minutes
   - Level: CRITICAL
   - Checks same card within time window

3. **Location Anomaly**
   - Pattern: New location not seen in last 24 hours
   - Level: INFO
   - Compares current location to recent history

4. **Unusual Pattern**
   - Pattern: 10+ transactions in 24 hours
   - Level: WARNING
   - Tracks high transaction volume

**Public Methods:**
- `analyzeOperation(OperationCarte)` - Auto-analyzes after each operation
- `getAllAlerts()` - Retrieve all alerts
- `getAlertsByLevel(AlertLevel)` - Filter by severity
- `getRecentAlerts(int)` - Get recent alerts
- `displayAlerts(List)` - Format and print alerts

### 4. Integration

#### OperationService.java
- Added `FraudService fraudService` field
- Calls `fraudService.analyzeOperation(operation)` after each successful operation
- Fraud detection runs automatically in background

#### Menu.java (Option 7)
New fraud analysis menu with 5 options:
1. View All Alerts
2. View Critical Alerts
3. View Warning Alerts
4. View Info Alerts
5. View Recent Alerts (Last 10)

## How It Works

### Automatic Detection Flow:
1. User performs operation via Menu (Option 5)
2. OperationService validates card and limits
3. Operation saved to database
4. **FraudService.analyzeOperation()** automatically runs all 4 detection rules
5. If suspicious pattern detected, FraudAlert saved to database
6. User can view alerts anytime via Menu (Option 7)

### Example Output:
```
========== FRAUD ALERTS ==========
[CRITICAL] Alert #1
  Operation ID: 45
  Reason: Rapid transactions: 4 operations in 5 minutes
  Detected: 2025-10-07T14:23:15
  ----------------------------------
[WARNING] Alert #2
  Operation ID: 46
  Reason: High amount transaction: $7500.00 at New York Store
  Detected: 2025-10-07T14:25:30
  ----------------------------------
[INFO] Alert #3
  Operation ID: 47
  Reason: New location detected: Paris ATM (usual locations: New York Store, Boston Mall)
  Detected: 2025-10-07T15:10:45
  ----------------------------------
```

## Detection Thresholds (Configurable in FraudService.java)
- HIGH_AMOUNT_THRESHOLD = $5,000
- RAPID_TRANSACTION_COUNT = 3 transactions
- RAPID_TRANSACTION_MINUTES = 5 minutes
- UNUSUAL_PATTERN_COUNT = 10 transactions
- UNUSUAL_PATTERN_HOURS = 24 hours

## Future Enhancements
- Auto-block card on CRITICAL alerts
- Email notifications to clients
- Machine learning for pattern detection
- Geographic distance calculation
- Time-of-day anomaly detection
- Merchant category analysis
