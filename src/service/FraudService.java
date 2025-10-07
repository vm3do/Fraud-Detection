package service;

import dao.FraudAlertDAO;
import dao.OperationDAO;
import dao.imp.FraudAlertDAOImp;
import dao.imp.OperationDAOImp;
import entity.AlertLevel;
import entity.FraudAlert;
import entity.OperationCarte;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FraudService {
    private final FraudAlertDAO fraudAlertDAO;
    private final OperationDAO operationDAO;

    // thresholds
    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("5000");
    private static final int RAPID_TRANSACTION_COUNT = 3;
    private static final int RAPID_TRANSACTION_MINUTES = 5;
    private static final BigDecimal DAILY_SPENDING_THRESHOLD = new BigDecimal("10000");

    public FraudService() {
        this.fraudAlertDAO = new FraudAlertDAOImp();
        this.operationDAO = new OperationDAOImp();
    }

    public void analyzeOperation(OperationCarte operation) {
        
        checkHighAmount(operation);

        checkRapidTransactions(operation);

        checkLocationAnomaly(operation);

        checkUnusualPattern(operation);
    }

    private void checkHighAmount(OperationCarte operation) {
        if (operation.amount().compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            String reason = String.format("High amount transaction: $%s at %s", 
                operation.amount(), operation.location());
            createAlert(operation.id(), AlertLevel.WARNING, reason);
        }
    }

    private void checkRapidTransactions(OperationCarte operation) {
        LocalDateTime startTime = operation.operationDate().minusMinutes(RAPID_TRANSACTION_MINUTES);
        int recentCount = operationDAO.countRecentOperationsByCardId(operation.cardId(), startTime);

        if (recentCount >= RAPID_TRANSACTION_COUNT) {
            String reason = String.format("Rapid transactions: %d operations in %d minutes", 
                recentCount, RAPID_TRANSACTION_MINUTES);
            createAlert(operation.id(), AlertLevel.CRITICAL, reason);
        }
    }

    private void checkLocationAnomaly(OperationCarte operation) {
        operationDAO.findLastOperationByCardId(operation.cardId()).ifPresent(lastOperation -> {
            if (!lastOperation.location().equals(operation.location())) {
                String reason = String.format("Location changed: %s -> %s", 
                    lastOperation.location(), operation.location());
                createAlert(operation.id(), AlertLevel.INFO, reason);
            }
        });
    }

    private void checkUnusualPattern(OperationCarte operation) {
        LocalDateTime startOfDay = operation.operationDate().toLocalDate().atStartOfDay();
        BigDecimal dailyTotal = operationDAO.calculateTotalAmountByCardId(operation.cardId(), startOfDay);

        if (dailyTotal.compareTo(DAILY_SPENDING_THRESHOLD) > 0) {
            String reason = String.format("Daily spending threshold exceeded: $%s", dailyTotal);
            createAlert(operation.id(), AlertLevel.WARNING, reason);
        }
    }

    private void createAlert(int operationId, AlertLevel level, String reason) {
        FraudAlert alert = new FraudAlert(0, operationId, level, reason, LocalDateTime.now());
        fraudAlertDAO.save(alert);
    }

    public List<FraudAlert> getAllAlerts() {
        return fraudAlertDAO.findAll();
    }

    public List<FraudAlert> getAlertsByLevel(AlertLevel level) {
        return fraudAlertDAO.findByLevel(level);
    }

    public List<FraudAlert> getRecentAlerts(int limit) {
        return fraudAlertDAO.findRecent(limit);
    }

    public void displayAlerts(List<FraudAlert> alerts) {
        if (alerts.isEmpty()) {
            System.out.println("No fraud alerts found.");
            return;
        }

        System.out.println("\n========== FRAUD ALERTS ==========");
        alerts.forEach(alert -> {
            System.out.printf("[%s] Alert #%d%n", alert.level(), alert.id());
            System.out.printf("  Operation ID: %d%n", alert.operationId());
            System.out.printf("  Reason: %s%n", alert.reason());
            System.out.printf("  Detected: %s%n", alert.detectedAt());
            System.out.println("  ----------------------------------");
        });
    }

    public FraudAlertDAO getFraudAlertDAO() {
        return fraudAlertDAO;
    }
}
