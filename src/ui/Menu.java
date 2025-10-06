package ui;

import entity.Client;
import service.ClientService;

import java.util.Scanner;

public class Menu {

    ClientService clientService = new ClientService();

    public void save() {

        System.out.println("===== FRAUD DETECTION SYSTEM =====");
        System.out.println("1. Create a Client");
        System.out.println("2. Issue a Card (Debit, Credit, Prepaid)");
        System.out.println("3. Perform an Operation (Purchase, Withdrawal, Online Payment)");
        System.out.println("4. View Card History");
        System.out.println("5. Launch Fraud Analysis");
        System.out.println("6. Block/Suspend a Card");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");

        try (Scanner scanner = new Scanner(System.in)) {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Create a Client ---");
                    System.out.print("Enter Client name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Client email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Client phone: ");
                    String phone = scanner.nextLine();

                    Client client = new Client(0, name, email, phone);
                    clientService.addClient(client);
                    break;

                case 2:
                    System.out.println("\n--- Issue a Card ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 3:
                    System.out.println("\n--- Perform an Operation ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 4:
                    System.out.println("\n--- View Card History ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 5:
                    System.out.println("\n--- Launch Fraud Analysis ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 6:
                    System.out.println("\n--- Block/Suspend a Card ---");
                    System.out.println("Feature coming soon...");
                    break;

                case 0:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
