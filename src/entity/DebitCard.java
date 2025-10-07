package entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class DebitCard extends Card {
    private final BigDecimal dailyLimit;

    public DebitCard(int id, String number, LocalDate expirationDate, CardStatus status, int clientId,
            BigDecimal dailyLimit) {
        super(id, number, expirationDate, status, CardType.DEBIT, clientId);
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    @Override
    public String toString() {
        return "DebitCard{" +
                "id=" + getId() +
                ", number='" + getNumber() + '\'' +
                ", expirationDate=" + getExpirationDate() +
                ", status=" + getStatus() +
                ", clientId=" + getClientId() +
                ", dailyLimit=" + dailyLimit +
                '}';
    }
}
