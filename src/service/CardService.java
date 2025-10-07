package service;

import dao.CardDAO;
import dao.ClientDAO;
import dao.imp.CardDAOImp;
import dao.imp.ClientDAOImp;
import entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CardService {

    private final CardDAO cardDAO = new CardDAOImp();
    private final ClientDAO clientDAO = new ClientDAOImp();
    private final Random random = new Random();

    public String generateCardNumber(CardType type) {
        String cardNumber;
        do {
            String firstDigit = switch (type) {
                case DEBIT -> "4";
                case CREDIT -> "5";
                case PREPAID -> "6";
            };

            StringBuilder numberBuilder = new StringBuilder(firstDigit);
            for (int i = 0; i < 15; i++) {
                numberBuilder.append(random.nextInt(10));
            }

            cardNumber = numberBuilder.toString();

        } while (cardDAO.findByNumber(cardNumber).isPresent());

        return cardNumber;
    }

    public void issueDebitCard(int clientId, BigDecimal dailyLimit) {
        if (!validateClient(clientId)) {
            return;
        }

        String cardNumber = generateCardNumber(CardType.DEBIT);
        LocalDate expirationDate = LocalDate.now().plusYears(3);

        DebitCard card = new DebitCard(0, cardNumber, expirationDate, CardStatus.ACTIVE, clientId, dailyLimit);

        if (cardDAO.save(card)) {
            System.out.println("Debit Card issued successfully!");
            System.out.println("Card Number: " + formatCardNumber(cardNumber));
            System.out.println("Expiration Date: " + expirationDate);
            System.out.println("Daily Limit: $" + dailyLimit);
        } else {
            System.out.println("Error issuing debit card. Please try again.");
        }
    }

    public void issueCreditCard(int clientId, BigDecimal monthlyLimit, BigDecimal interestRate) {
        if (!validateClient(clientId)) {
            return;
        }

        String cardNumber = generateCardNumber(CardType.CREDIT);
        LocalDate expirationDate = LocalDate.now().plusYears(3);

        CreditCard card = new CreditCard(0, cardNumber, expirationDate, CardStatus.ACTIVE, clientId,
                monthlyLimit, interestRate);

        if (cardDAO.save(card)) {
            System.out.println("Credit Card issued successfully!");
            System.out.println("Card Number: " + formatCardNumber(cardNumber));
            System.out.println("Expiration Date: " + expirationDate);
            System.out.println("Monthly Limit: $" + monthlyLimit);
            System.out.println("Interest Rate: " + interestRate + "%");
        } else {
            System.out.println("Error issuing credit card. Please try again.");
        }
    }

    public void issuePrepaidCard(int clientId, BigDecimal initialBalance) {
        if (!validateClient(clientId)) {
            return;
        }

        String cardNumber = generateCardNumber(CardType.PREPAID);
        LocalDate expirationDate = LocalDate.now().plusYears(3);

        PrepaidCard card = new PrepaidCard(0, cardNumber, expirationDate, CardStatus.ACTIVE, clientId, initialBalance);

        if (cardDAO.save(card)) {
            System.out.println("Prepaid Card issued successfully!");
            System.out.println("Card Number: " + formatCardNumber(cardNumber));
            System.out.println("Expiration Date: " + expirationDate);
            System.out.println("Initial Balance: $" + initialBalance);
        } else {
            System.out.println("Error issuing prepaid card. Please try again.");
        }
    }

    public void changeCardStatus(int cardId, CardStatus newStatus) {
        Optional<Card> card = cardDAO.findById(cardId);

        if (card.isEmpty()) {
            System.out.println("Card not found.");
            return;
        }

        if (cardDAO.updateStatus(cardId, newStatus)) {
            String action = switch (newStatus) {
                case BLOCKED -> "blocked";
                case SUSPENDED -> "suspended";
                case ACTIVE -> "activated";
            };
            System.out.println("Card " + action + " successfully.");
        } else {
            System.out.println("Error updating card status.");
        }
    }

    private boolean validateClient(int clientId) {
        Optional<Client> client = clientDAO.findById(clientId);
        if (client.isEmpty()) {
            System.out.println("Client not found.");
            return false;
        }
        return true;
    }

    public List<Card> getClientCards(int clientId) {
        return cardDAO.findByClientId(clientId);
    }

    public void displayClientCards(int clientId) {
        List<Card> cards = getClientCards(clientId);

        if (cards.isEmpty()) {
            System.out.println("No cards found for this client.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         CLIENT CARDS                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");

        for (Card card : cards) {
            displayCard(card);
            System.out.println("─────────────────────────────────────────────────────────────────────");
        }

        System.out.println("Total cards: " + cards.size());
    }

    public void displayCard(Card card) {
        System.out.println("Card ID:     " + card.getId());
        System.out.println("Number:      " + formatCardNumber(card.getNumber()));
        System.out.println("Type:        " + card.getType());
        System.out.println("Status:      " + card.getStatus());
        System.out.println("Expiration:  " + card.getExpirationDate());

        if (card instanceof DebitCard debitCard) {
            System.out.println("Daily Limit: $" + debitCard.getDailyLimit());
        } else if (card instanceof CreditCard creditCard) {
            System.out.println("Monthly Limit: $" + creditCard.getMonthlyLimit());
            System.out.println("Interest Rate: " + creditCard.getInterestRate() + "%");
        } else if (card instanceof PrepaidCard prepaidCard) {
            System.out.println("Balance:     $" + prepaidCard.getBalance());
        }
    }

    private String formatCardNumber(String number) {
        if (number.length() != 16) {
            return number;
        }
        return number.substring(0, 4) + "-" +
                number.substring(4, 8) + "-" +
                number.substring(8, 12) + "-" +
                number.substring(12, 16);
    }

    public CardDAO getCardDAO() {
        return cardDAO;
    }
}
