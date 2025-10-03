package dao.imp;

import dao.ClientDAO;
import entity.Client;
import config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClientDAOImp implements ClientDAO {

    private final Connection connection = DBConnection.getInstance().getConnection();

    public boolean save(Client client){
        String sql = "INSERT INTO clients (name, email, phone) VALUES (?, ?, ?)";
        try{
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getPhone());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
        return false;
    }

    public Optional<Client> findById(int id){
        return findByField("id", id);
    }

    public Optional<Client> findByName(String name){
        return findByField("name", name);
    }

    public Optional<Client> findByEmail(String email){
        return findByField("email", email);
    }

    public Optional<Client> findByPhone(String phone){
        return findByField("phone", phone);
    }

    public List<Client> findAll(){
        String sql = "SELECT * FROM clients";
        try{

        } catch {

        }
    }




    private Optional<Client> findByField(String field, String value) {
        String sql = "SELECT * FROM clients WHERE " + field + " = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, value);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                Client client = new Client(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("email"),
                        result.getString("phone")
                );
                return Optional.of(client);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching client by " + field + ": " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<Client> findByField(String field, int value) {
        String sql = "SELECT * FROM clients WHERE " + field + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, value);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                Client client = new Client(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("email"),
                        result.getString("phone")
                );
                return Optional.of(client);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching client by " + field + ": " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }



}
