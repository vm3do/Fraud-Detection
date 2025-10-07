package ui;

import entity.AlertLevel;
import entity.CardStatus;
import entity.Client;
import entity.FraudAlert;
import entity.OperationType;
import service.ClientService;
import service.CardService;
import service.FraudService;
import service.OperationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Menu {

    ClientService clientService = new ClientService();
    CardService cardService = new CardService();
    OperationService operationService = new OperationService();
    FraudService fraudService = new FraudService();
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\n===== FRAUD DETECTION SYSTEM =====");
            System.out.println("1. Create a Client");
            System.out.println("2. View All Clients");
            System.out.println("3. Find a Client");
            System.out.println("4. Issue a Card (Debit, Credit, Prepaid)");
            System.out.println("5. Perform an Operation (Purchase, Withdrawal, Online Payment)");
            System.out.println("6. View Card History");
            System.out.println("7. Launch Fraud Analysis");
            System.out.println("8. Block/Suspend a Card");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createClient();
                    break;

                case 2:
                    viewAllClients();
                    break;

                case 3:
                    findClient();
                    break;

                case 4:
                    issueCard();
                    break;

                case 5:
                    performOperation();
                    break;

                case 6:
                    viewCardHistory();
                    break;

                case 7:
                    launchFraudAnalysis();
                    break;

                case 8:
                    blockOrSuspendCard();
                    break;

                case 0:
                    System.out.println("Exiting...");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    private void createClient() {
        System.out.println("\n--- Create a Client ---");
        System.out.print("Enter Client name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Client email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Client phone: ");
        String phone = scanner.nextLine();

        Client client = new Client(0, name, email, phone);
        clientService.addClient(client);
    }

    private void viewAllClients() {
        System.out.println("\n--- View All Clients ---");
        clientService.displayAllClients();
    }

    private void findClient() {
        System.out.println("\n--- Find a Client ---");
        System.out.println("Search by:");
        System.out.println("1. Email");
        System.out.println("2. ID");
        System.out.print("Choose search option: ");

        int searchOption = scanner.nextInt();
        scanner.nextLine();

        Optional<Client> foundClient = Optional.empty();

        switch (searchOption) {
            case 1:
                System.out.print("Enter email: ");
                String email = scanner.nextLine();
                foundClient = clientService.getClientDAO().findByEmail(email);
                break;

            case 2:
                System.out.print("Enter ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                foundClient = clientService.getClientDAO().findById(id);
                break;

            default:
                System.out.println("Invalid search option.");
                return;
        }

        if (foundClient.isPresent()) {
            clientService.displayClient(foundClient.get());
        } else {
            System.out.println("✗ Client not found.");
        }
    }

    private void issueCard() {
        System.out.println("\n--- Issue a Card ---");
        System.out.print("Enter Client ID: ");
        int clientId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\nSelect Card Type:");
        System.out.println("1. Debit Card");
        System.out.println("2. Credit Card");
        System.out.println("3. Prepaid Card");
        System.out.print("Choose card type: ");

        int cardType = scanner.nextInt();
        scanner.nextLine();

        switch (cardType) {
            case 1:
                System.out.print("Enter daily limit: $");
                BigDecimal dailyLimit = scanner.nextBigDecimal();
                scanner.nextLine();
                cardService.issueDebitCard(clientId, dailyLimit);
                break;

            case 2:
                System.out.print("Enter monthly limit: $");
                BigDecimal monthlyLimit = scanner.nextBigDecimal();
                scanner.nextLine();
                System.out.print("Enter interest rate (%): ");
                BigDecimal interestRate = scanner.nextBigDecimal();
                scanner.nextLine();
                cardService.issueCreditCard(clientId, monthlyLimit, interestRate);
                break;

            case 3:
                System.out.print("Enter initial balance: $");
                BigDecimal initialBalance = scanner.nextBigDecimal();
                scanner.nextLine();
                cardService.issuePrepaidCard(clientId, initialBalance);
                break;

            default:
                System.out.println("Invalid card type.");
        }
    }

    private void viewCardHistory() {
        System.out.println("\n--- View Card History ---");
        System.out.print("Enter Card ID: ");
        int cardId = scanner.nextInt();
        scanner.nextLine();

        operationService.displayCardOperations(cardId);
    }

    private void performOperation() {
        System.out.println("\n--- Perform an Operation ---");
        System.out.print("Enter Card ID: ");
        int cardId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\nSelect Operation Type:");
        System.out.println("1. Purchase");
        System.out.println("2. Withdrawal");
        System.out.println("3. Online Payment");
        System.out.print("Choose type: ");

        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        OperationType operationType = switch (typeChoice) {
            case 1 -> OperationType.PURCHASE;
            case 2 -> OperationType.WITHDRAWAL;
            case 3 -> OperationType.ONLINEPAYMENT;
            default -> null;
        };

        if (operationType == null) {
            System.out.println("Invalid operation type.");
            return;
        }

        System.out.print("Enter amount: $");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        System.out.print("Enter location: ");
        String location = scanner.nextLine();

        operationService.performOperation(cardId, operationType, amount, location);
    }

    private void blockOrSuspendCard() {
        System.out.println("\n--- Block/Suspend a Card ---");
        System.out.print("Enter Card ID: ");
        int cardId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\nSelect action:");
        System.out.println("1. Block Card");
        System.out.println("2. Suspend Card");
        System.out.println("3. Activate Card");
        System.out.print("Choose action: ");

        int action = scanner.nextInt();
        scanner.nextLine();

        CardStatus newStatus = switch (action) {
            case 1 -> CardStatus.BLOCKED;
            case 2 -> CardStatus.SUSPENDED;
            case 3 -> CardStatus.ACTIVE;
            default -> null;
        };

        if (newStatus != null) {
            cardService.changeCardStatus(cardId, newStatus);
        } else {
            System.out.println("Invalid action.");
        }
    }

    private void launchFraudAnalysis() {
        System.out.println("\n--- Fraud Analysis ---");
        System.out.println("1. View All Alerts");
        System.out.println("2. View Critical Alerts");
        System.out.println("3. View Warning Alerts");
        System.out.println("4. View Info Alerts");
        System.out.println("5. View Recent Alerts (Last 10)");
        System.out.print("Choose an option: ");

        int option = scanner.nextInt();
        scanner.nextLine();

        List<FraudAlert> alerts = switch (option) {
            case 1 -> fraudService.getAllAlerts();
            case 2 -> fraudService.getAlertsByLevel(AlertLevel.CRITICAL);
            case 3 -> fraudService.getAlertsByLevel(AlertLevel.WARNING);
            case 4 -> fraudService.getAlertsByLevel(AlertLevel.INFO);
            case 5 -> fraudService.getRecentAlerts(10);
            default -> {
                System.out.println("Invalid option.");
                yield List.of();
            }
        };

        fraudService.displayAlerts(alerts);
    }
}
