package service;

import dao.ClientDAO;
import dao.imp.ClientDAOImp;
import entity.Client;

import java.util.List;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAOImp();

    public void addClient(Client client) {

        if (clientDAO.findByEmail(client.email()).isPresent()) {
            System.out.println("Email already exists");
            return;
        }

        if (client.name() == null || client.name().length() < 2) {
            System.out.println("Enter a valid name (at least 2 characters)");
            return;
        }

        if (client.email() == null || !client.email().contains("@")) {
            System.out.println("Enter a valid email");
            return;
        }

        if (clientDAO.save(client)) {
            System.out.println("Client has been added successfully");
        } else {
            System.out.println("Error while saving, please try again");
        }
    }

    public ClientDAO getClientDAO() {
        return clientDAO;
    }

    public void displayAllClients() {
        List<Client> clients = clientDAO.findAll();

        if (clients.isEmpty()) {
            System.out.println("No clients found in the system.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         ALL CLIENTS                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.printf("%-5s %-20s %-30s %-15s%n", "ID", "Name", "Email", "Phone");
        System.out.println("─────────────────────────────────────────────────────────────────────");

        for (Client client : clients) {
            System.out.printf("%-5d %-20s %-30s %-15s%n",
                    client.id(),
                    client.name(),
                    client.email(),
                    client.phone());
        }

        System.out.println("─────────────────────────────────────────────────────────────────────");
        System.out.println("Total clients: " + clients.size());
    }

    public void displayClient(Client client) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       CLIENT DETAILS                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println("ID:     " + client.id());
        System.out.println("Name:   " + client.name());
        System.out.println("Email:  " + client.email());
        System.out.println("Phone:  " + client.phone());
        System.out.println("─────────────────────────────────────────────────────────────────────");
    }
}
