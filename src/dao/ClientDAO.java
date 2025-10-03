package dao;

import entity.Client;
import java.util.List;
import java.util.Optional;

public interface ClientDAO {

    public boolean save(Client client);
    public List<Client> findAll();
    public Optional<Client> findById(int id);
    public Optional<Client> findByEmail(String email);
}
