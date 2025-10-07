package dao.imp;

import dao.CardDAO;
import entity.*;
import config.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDAOImp implements CardDAO {

    private final Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public boolean save(Card card) {
        String sql = "INSERT INTO cards (number, expirationDate, status, type, clientId, dailyLimit, monthlyLimit, interestRate, balance) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, card.getNumber());
            stmt.setDate(2, Date.valueOf(card.getExpirationDate()));
            stmt.setString(3, card.getStatus().name());
            stmt.setString(4, card.getType().name());
            stmt.setInt(5, card.getClientId());

            if (card instanceof DebitCard debitCard) {
                stmt.setBigDecimal(6, debitCard.getDailyLimit());
                stmt.setNull(7, Types.DECIMAL);
                stmt.setNull(8, Types.DECIMAL);
                stmt.setNull(9, Types.DECIMAL);
            } else if (card instanceof CreditCard creditCard) {
                stmt.setNull(6, Types.DECIMAL);
                stmt.setBigDecimal(7, creditCard.getMonthlyLimit());
                stmt.setBigDecimal(8, creditCard.getInterestRate());
                stmt.setNull(9, Types.DECIMAL);
            } else if (card instanceof PrepaidCard prepaidCard) {
                stmt.setNull(6, Types.DECIMAL);
                stmt.setNull(7, Types.DECIMAL);
                stmt.setNull(8, Types.DECIMAL);
                stmt.setBigDecimal(9, prepaidCard.getBalance());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error saving card: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Card> findById(int id) {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return Optional.of(mapResultSetToCard(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching card by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Card> findByNumber(String number) {
        String sql = "SELECT * FROM cards WHERE number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, number);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return Optional.of(mapResultSetToCard(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching card by number: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Card> findByClientId(int clientId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE clientId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                cards.add(mapResultSetToCard(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching cards by client ID: " + e.getMessage());
        }
        return cards;
    }

    @Override
    public List<Card> findAll() {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                cards.add(mapResultSetToCard(result));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all cards: " + e.getMessage());
        }
        return cards;
    }

    @Override
    public boolean updateStatus(int cardId, CardStatus newStatus) {
        String sql = "UPDATE cards SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, cardId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating card status: " + e.getMessage());
            return false;
        }
    }

    private Card mapResultSetToCard(ResultSet result) throws SQLException {
        int id = result.getInt("id");
        String number = result.getString("number");
        LocalDate expirationDate = result.getDate("expirationDate").toLocalDate();
        CardStatus status = CardStatus.valueOf(result.getString("status"));
        CardType type = CardType.valueOf(result.getString("type"));
        int clientId = result.getInt("clientId");

        return switch (type) {
            case DEBIT -> {
                BigDecimal dailyLimit = result.getBigDecimal("dailyLimit");
                yield new DebitCard(id, number, expirationDate, status, clientId, dailyLimit);
            }
            case CREDIT -> {
                BigDecimal monthlyLimit = result.getBigDecimal("monthlyLimit");
                BigDecimal interestRate = result.getBigDecimal("interestRate");
                yield new CreditCard(id, number, expirationDate, status, clientId, monthlyLimit, interestRate);
            }
            case PREPAID -> {
                BigDecimal balance = result.getBigDecimal("balance");
                yield new PrepaidCard(id, number, expirationDate, status, clientId, balance);
            }
        };
    }
}
