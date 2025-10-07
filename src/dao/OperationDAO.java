package dao;

import entity.OperationCarte;
import entity.OperationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OperationDAO {

    boolean save(OperationCarte operation);

    Optional<OperationCarte> findById(int id);

    List<OperationCarte> findByCardId(int cardId);

    List<OperationCarte> findByType(OperationType type);

    List<OperationCarte> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<OperationCarte> findAll();
}
