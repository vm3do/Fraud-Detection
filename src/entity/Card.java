package entity;

import java.time.LocalDate;

public sealed abstract class Card permits DebitCard, CreditCard, PrepaidCard {
    private final int id;
    private final String number;
    private final LocalDate expirationDate;
    private final CardStatus status;
    private final CardType type;
    private final int clientId;

    public Card(int id, String number, LocalDate expirationDate, CardStatus status, CardType type, int clientId) {
        this.id = id;
        this.number = number;
        this.expirationDate = expirationDate;
        this.status = status;
        this.type = type;
        this.clientId = clientId;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public CardType getType() {
        return type;
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", expirationDate=" + expirationDate +
                ", status=" + status +
                ", type=" + type +
                ", clientId=" + clientId +
                '}';
    }
}
