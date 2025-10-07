package ui;

import entity.Client;
import service.ClientService;

import java.util.Optional;
import java.util.Scanner;

public class Menu {

    ClientService clientService = new ClientService();
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
                    System.out.println("\n--- Issue a Card ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 5:
                    System.out.println("\n--- Perform an Operation ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 6:
                    System.out.println("\n--- View Card History ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 7:
                    System.out.println("\n--- Launch Fraud Analysis ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 8:
                    System.out.println("\n--- Block/Suspend a Card ---");
                    System.out.println("Feature coming soon...");
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
}
