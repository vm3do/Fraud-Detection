package dao;

import entity.AlertLevel;
import entity.FraudAlert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FraudAlertDAO {
    void save(FraudAlert alert);
    Optional<FraudAlert> findById(int id);
    List<FraudAlert> findAll();
    List<FraudAlert> findByOperationId(int operationId);
    List<FraudAlert> findByLevel(AlertLevel level);
    List<FraudAlert> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<FraudAlert> findRecent(int limit);
}
