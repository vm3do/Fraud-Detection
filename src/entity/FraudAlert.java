package entity;

import java.time.LocalDateTime;

public record FraudAlert(
    int id,
    int operationId,
    AlertLevel level,
    String reason,
    LocalDateTime detectedAt
) {
}
