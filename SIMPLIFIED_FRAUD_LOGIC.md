# Fraud Detection - Simplified Logic

## Changes Made

### 1. OperationDAO - New Helper Methods
Added 3 optimized methods to reduce Java processing and leverage SQL:

```java
Optional<OperationCarte> findLastOperationByCardId(int cardId);
int countRecentOperationsByCardId(int cardId, LocalDateTime since);
BigDecimal calculateTotalAmountByCardId(int cardId, LocalDateTime since);
```

### 2. Simplified Detection Rules

#### Before vs After Comparison:

**Rapid Transactions**
- BEFORE: Fetch all operations in 5-min window, filter by cardId in Java, count with stream
- AFTER: Single SQL COUNT query filtered by cardId and date
- Result: Faster, cleaner, less memory

**Location Anomaly**
- BEFORE: Fetch 24 hours of operations, build frequency map, check if location exists
- AFTER: Get last operation, compare locations directly
- Result: Single query, simple comparison, more accurate

**Unusual Pattern**
- BEFORE: Fetch 24 hours of operations, count them, sum amounts, check both conditions
- AFTER: Calculate daily total with SQL SUM, check against threshold
- Result: Single query, clear business rule

### 3. Updated Thresholds
```java
HIGH_AMOUNT_THRESHOLD = $5,000        // Single transaction
RAPID_TRANSACTION_COUNT = 3           // Within 5 minutes
RAPID_TRANSACTION_MINUTES = 5         // Time window
DAILY_SPENDING_THRESHOLD = $10,000    // Per day spending
```

## Code Comparison

### Location Check - BEFORE (Complex)
```java
private void checkLocationAnomaly(OperationCarte operation) {
    LocalDateTime yesterday = operation.operationDate().minusHours(24);
    List<OperationCarte> recentOps = operationDAO.findByDateRange(yesterday, operation.operationDate())
        .stream()
        .filter(op -> op.cardId() == operation.cardId())
        .toList();

    if (!recentOps.isEmpty()) {
        Map<String, Long> locationFrequency = recentOps.stream()
            .collect(Collectors.groupingBy(OperationCarte::location, Collectors.counting()));

        String currentLocation = operation.location();
        if (!locationFrequency.containsKey(currentLocation) && locationFrequency.size() > 0) {
            String reason = String.format("New location detected: %s (usual locations: %s)", 
                currentLocation, String.join(", ", locationFrequency.keySet()));
            createAlert(operation.id(), AlertLevel.INFO, reason);
        }
    }
}
```

### Location Check - AFTER (Simple)
```java
private void checkLocationAnomaly(OperationCarte operation) {
    operationDAO.findLastOperationByCardId(operation.cardId()).ifPresent(lastOperation -> {
        if (!lastOperation.location().equals(operation.location())) {
            String reason = String.format("Location changed: %s -> %s", 
                lastOperation.location(), operation.location());
            createAlert(operation.id(), AlertLevel.INFO, reason);
        }
    });
}
```

### Unusual Pattern - BEFORE (Complex)
```java
private void checkUnusualPattern(OperationCarte operation) {
    LocalDateTime last24Hours = operation.operationDate().minusHours(24);
    List<OperationCarte> recentOps = operationDAO.findByDateRange(last24Hours, operation.operationDate())
        .stream()
        .filter(op -> op.cardId() == operation.cardId())
        .toList();

    if (recentOps.size() >= 10) {
        BigDecimal totalAmount = recentOps.stream()
            .map(OperationCarte::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        String reason = String.format("Unusual activity: %d transactions totaling $%s in 24 hours", 
            recentOps.size(), totalAmount);
        createAlert(operation.id(), AlertLevel.WARNING, reason);
    }
}
```

### Unusual Pattern - AFTER (Simple)
```java
private void checkUnusualPattern(OperationCarte operation) {
    LocalDateTime startOfDay = operation.operationDate().toLocalDate().atStartOfDay();
    BigDecimal dailyTotal = operationDAO.calculateTotalAmountByCardId(operation.cardId(), startOfDay);

    if (dailyTotal.compareTo(DAILY_SPENDING_THRESHOLD) > 0) {
        String reason = String.format("Daily spending threshold exceeded: $%s", dailyTotal);
        createAlert(operation.id(), AlertLevel.WARNING, reason);
    }
}
```

## Benefits

### Performance
- Reduced data transfer from database
- SQL aggregations instead of Java streams
- Less object creation and memory usage

### Maintainability
- Clearer business rules
- Easier to understand and modify
- Less code to test and debug

### Accuracy
- Location check now detects actual movement patterns
- Daily spending matches real business day (not rolling 24 hours)
- More intuitive thresholds

## Detection Examples

**Scenario 1: Location Change**
- Operation 1: New York Store
- Operation 2: New York Store (no alert)
- Operation 3: Paris ATM (INFO alert: "Location changed: New York Store -> Paris ATM")

**Scenario 2: Rapid Transactions**
- 3 operations within 5 minutes (CRITICAL alert)

**Scenario 3: Daily Spending**
- Daily total reaches $10,000 (WARNING alert)

**Scenario 4: High Amount**
- Single $7,500 transaction (WARNING alert)
