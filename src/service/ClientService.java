package service;

import dao.ClientDAO;
import dao.imp.ClientDAOImp;
import entity.Client;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAOImp();

    public void addClient(Client client){

        if(clientDAO.findByEmail(client.email()).isPresent()){
            System.out.println("email already exist");
            return;
        }

        if(client.name() == null || client.name().length() < 2){
            System.out.println("enter a valid name");
            return;
        }

        if(client.email() == null || !client.email().contains("@")){
            System.out.println("enter a valid email");
            return;
        }

        if(clientDAO.save(client)){
            System.out.println("client has been added");
        } else {
            System.out.println("error while saving, please try again");
        }
    }
}
