package service;

import dao.CardDAO;
import dao.OperationDAO;
import dao.imp.CardDAOImp;
import dao.imp.OperationDAOImp;
import entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OperationService {

    private final OperationDAO operationDAO = new OperationDAOImp();
    private final CardDAO cardDAO = new CardDAOImp();
    private final FraudService fraudService = new FraudService();

    public void performOperation(int cardId, OperationType type, BigDecimal amount, String location) {
        Optional<Card> cardOpt = cardDAO.findById(cardId);

        if (cardOpt.isEmpty()) {
            System.out.println("Card not found.");
            return;
        }

        Card card = cardOpt.get();

        if (card.getStatus() != CardStatus.ACTIVE) {
            System.out.println("Card is not active. Status: " + card.getStatus());
            return;
        }

        if (!checkLimit(card, amount)) {
            return;
        }

        OperationCarte operation = new OperationCarte(
                0,
                LocalDateTime.now(),
                amount,
                type,
                location,
                cardId);

        if (operationDAO.save(operation)) {
            System.out.println("Operation completed successfully!");
            System.out.println("Type: " + type);
            System.out.println("Amount: $" + amount);
            System.out.println("Location: " + location);
            System.out.println("Date: " + operation.operationDate());
            
            fraudService.analyzeOperation(operation);
        } else {
            System.out.println("Error performing operation. Please try again.");
        }
    }

    private boolean checkLimit(Card card, BigDecimal amount) {
        if (card instanceof DebitCard debitCard) {
            BigDecimal dailyTotal = calculateDailyTotal(card.getId());
            BigDecimal newTotal = dailyTotal.add(amount);

            if (newTotal.compareTo(debitCard.getDailyLimit()) > 0) {
                System.out.println("Daily limit exceeded!");
                System.out.println("Daily Limit: $" + debitCard.getDailyLimit());
                System.out.println("Already spent today: $" + dailyTotal);
                System.out.println("Transaction amount: $" + amount);
                return false;
            }

        } else if (card instanceof CreditCard creditCard) {
            BigDecimal monthlyTotal = calculateMonthlyTotal(card.getId());
            BigDecimal newTotal = monthlyTotal.add(amount);

            if (newTotal.compareTo(creditCard.getMonthlyLimit()) > 0) {
                System.out.println("Monthly limit exceeded!");
                System.out.println("Monthly Limit: $" + creditCard.getMonthlyLimit());
                System.out.println("Already spent this month: $" + monthlyTotal);
                System.out.println("Transaction amount: $" + amount);
                return false;
            }

        } else if (card instanceof PrepaidCard prepaidCard) {
            if (amount.compareTo(prepaidCard.getBalance()) > 0) {
                System.out.println("Insufficient balance!");
                System.out.println("Available Balance: $" + prepaidCard.getBalance());
                System.out.println("Transaction amount: $" + amount);
                return false;
            }
        }

        return true;
    }

    private BigDecimal calculateDailyTotal(int cardId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<OperationCarte> todayOperations = operationDAO.findByDateRange(startOfDay, endOfDay)
                .stream()
                .filter(op -> op.cardId() == cardId)
                .toList();

        return todayOperations.stream()
                .map(OperationCarte::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMonthlyTotal(int cardId) {
        LocalDate now = LocalDate.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        List<OperationCarte> monthOperations = operationDAO.findByDateRange(startOfMonth, endOfMonth)
                .stream()
                .filter(op -> op.cardId() == cardId)
                .toList();

        return monthOperations.stream()
                .map(OperationCarte::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void displayCardOperations(int cardId) {
        List<OperationCarte> operations = operationDAO.findByCardId(cardId);

        if (operations.isEmpty()) {
            System.out.println("No operations found for this card.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      CARD OPERATIONS                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.printf("%-5s %-20s %-15s %-12s %-20s%n",
                "ID", "Date", "Type", "Amount", "Location");
        System.out.println("─────────────────────────────────────────────────────────────────────");

        for (OperationCarte op : operations) {
            System.out.printf("%-5d %-20s %-15s $%-11.2f %-20s%n",
                    op.id(),
                    op.operationDate().toString().substring(0, 19),
                    op.operationType(),
                    op.amount(),
                    op.location());
        }

        System.out.println("─────────────────────────────────────────────────────────────────────");
        System.out.println("Total operations: " + operations.size());

        BigDecimal total = operations.stream()
                .map(OperationCarte::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total amount: $" + total);
    }

    public OperationDAO getOperationDAO() {
        return operationDAO;
    }
}
