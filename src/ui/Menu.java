package ui;

import entity.Client;
import service.ClientService;

import java.util.Scanner;

public class Menu {

    ClientService clientService = new ClientService();

    public void save() {

        System.out.println("Menu");
        System.out.println("1. Add Client");
        System.out.println("2. View Clients");
        System.out.println("3. Edit Client");
        System.out.println("4. Delete Client");
        System.out.println("5. Exit");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:

                System.out.println("Enter Client name");
                String name = scanner.nextLine();
                System.out.println("Enter Client email");
                String email = scanner.nextLine();
                System.out.println("Enter Client phone");
                String phone = scanner.nextLine();

                Client client = new Client(name, email, phone);

                clientService.addClient(client);
                break;

            case 0:
                System.out.println("exiting..");
                break;

            default:
                System.out.println("Invalid choice.");
                break;
        }
    }
}
