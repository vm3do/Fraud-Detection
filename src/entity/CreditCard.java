package entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class CreditCard extends Card {
    private final BigDecimal monthlyLimit;
    private final BigDecimal interestRate;

    public CreditCard(int id, String number, LocalDate expirationDate, CardStatus status, int clientId,
            BigDecimal monthlyLimit, BigDecimal interestRate) {
        super(id, number, expirationDate, status, CardType.CREDIT, clientId);
        this.monthlyLimit = monthlyLimit;
        this.interestRate = interestRate;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "id=" + getId() +
                ", number='" + getNumber() + '\'' +
                ", expirationDate=" + getExpirationDate() +
                ", status=" + getStatus() +
                ", clientId=" + getClientId() +
                ", monthlyLimit=" + monthlyLimit +
                ", interestRate=" + interestRate +
                '}';
    }
}
