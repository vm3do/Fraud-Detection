package dao.imp;

import config.DBConnection;
import dao.OperationDAO;
import entity.OperationCarte;
import entity.OperationType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperationDAOImp implements OperationDAO {

    private final Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public boolean save(OperationCarte operation) {
        String sql = "INSERT INTO CardOperation (operationDate, amount, operationType, location, cardId) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(operation.operationDate()));
            stmt.setBigDecimal(2, operation.amount());
            stmt.setString(3, operation.operationType().name());
            stmt.setString(4, operation.location());
            stmt.setInt(5, operation.cardId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error saving operation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<OperationCarte> findById(int id) {
        String sql = "SELECT * FROM CardOperation WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return Optional.of(mapResultSetToOperation(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching operation by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<OperationCarte> findByCardId(int cardId) {
        List<OperationCarte> operations = new ArrayList<>();
        String sql = "SELECT * FROM CardOperation WHERE cardId = ? ORDER BY operationDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                operations.add(mapResultSetToOperation(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching operations by card ID: " + e.getMessage());
        }
        return operations;
    }

    @Override
    public List<OperationCarte> findByType(OperationType type) {
        List<OperationCarte> operations = new ArrayList<>();
        String sql = "SELECT * FROM CardOperation WHERE operationType = ? ORDER BY operationDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                operations.add(mapResultSetToOperation(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching operations by type: " + e.getMessage());
        }
        return operations;
    }

    @Override
    public List<OperationCarte> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<OperationCarte> operations = new ArrayList<>();
        String sql = "SELECT * FROM CardOperation WHERE operationDate BETWEEN ? AND ? ORDER BY operationDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                operations.add(mapResultSetToOperation(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching operations by date range: " + e.getMessage());
        }
        return operations;
    }

    @Override
    public List<OperationCarte> findAll() {
        List<OperationCarte> operations = new ArrayList<>();
        String sql = "SELECT * FROM CardOperation ORDER BY operationDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                operations.add(mapResultSetToOperation(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all operations: " + e.getMessage());
        }
        return operations;
    }

    private OperationCarte mapResultSetToOperation(ResultSet result) throws SQLException {
        return new OperationCarte(
                result.getInt("id"),
                result.getTimestamp("operationDate").toLocalDateTime(),
                result.getBigDecimal("amount"),
                OperationType.valueOf(result.getString("operationType")),
                result.getString("location"),
                result.getInt("cardId"));
    }
}
