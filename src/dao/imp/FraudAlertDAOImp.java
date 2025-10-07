package dao.imp;

import config.DBConnection;
import dao.FraudAlertDAO;
import entity.AlertLevel;
import entity.FraudAlert;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FraudAlertDAOImp implements FraudAlertDAO {

    @Override
    public void save(FraudAlert alert) {
        String sql = "INSERT INTO FraudAlert (operation_id, level, reason, detected_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alert.operationId());
            ps.setString(2, alert.level().name());
            ps.setString(3, alert.reason());
            ps.setTimestamp(4, Timestamp.valueOf(alert.detectedAt()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<FraudAlert> findById(int id) {
        String sql = "SELECT * FROM FraudAlert WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToFraudAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<FraudAlert> findAll() {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM FraudAlert ORDER BY detected_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                alerts.add(mapToFraudAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public List<FraudAlert> findByOperationId(int operationId) {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM FraudAlert WHERE operation_id = ? ORDER BY detected_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                alerts.add(mapToFraudAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public List<FraudAlert> findByLevel(AlertLevel level) {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM FraudAlert WHERE level = ? ORDER BY detected_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, level.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                alerts.add(mapToFraudAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public List<FraudAlert> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM FraudAlert WHERE detected_at BETWEEN ? AND ? ORDER BY detected_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                alerts.add(mapToFraudAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public List<FraudAlert> findRecent(int limit) {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM FraudAlert ORDER BY detected_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                alerts.add(mapToFraudAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    private FraudAlert mapToFraudAlert(ResultSet rs) throws SQLException {
        return new FraudAlert(
            rs.getInt("id"),
            rs.getInt("operation_id"),
            AlertLevel.valueOf(rs.getString("level")),
            rs.getString("reason"),
            rs.getTimestamp("detected_at").toLocalDateTime()
        );
    }
}
