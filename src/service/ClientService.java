package service;

import dao.ClientDAO;
import dao.imp.ClientDAOImp;
import entity.Client;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAOImp();

    public boolean addClient(Client client){

        if(clientDAO.findByEmail(client.getEmail()).isPresent()){
            System.out.println("email already exist");
            return false;
        }

        if(client.getName() == null || client.getName().length() < 2){
            System.out.println("enter a valid name");
            return false;
        }

        if(client.getEmail() == null || !client.getEmail().contains("@")){
            System.out.println("enter a valid email");
            return false;
        }

        if(clientDAO.save(client)){
            System.out.println("client has been added");
        }
        return true;
    }
}
