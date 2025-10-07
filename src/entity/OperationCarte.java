package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperationCarte(
        int id,
        LocalDateTime operationDate,
        BigDecimal amount,
        OperationType operationType,
        String location,
        int cardId) {
}
