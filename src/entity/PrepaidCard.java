package entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class PrepaidCard extends Card {
    private final BigDecimal balance;

    public PrepaidCard(int id, String number, LocalDate expirationDate, CardStatus status, int clientId,
            BigDecimal balance) {
        super(id, number, expirationDate, status, CardType.PREPAID, clientId);
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "PrepaidCard{" +
                "id=" + getId() +
                ", number='" + getNumber() + '\'' +
                ", expirationDate=" + getExpirationDate() +
                ", status=" + getStatus() +
                ", clientId=" + getClientId() +
                ", balance=" + balance +
                '}';
    }
}
