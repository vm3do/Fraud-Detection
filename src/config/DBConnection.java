package config;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance = null;
        private Connection connection;


        private DBConnection() {
            try{
                String URL = "jdbc:mysql://127.0.0.1:3306/fraud_detection";
                String USERNAME = "root";
                String PASSWORD = "NewPass123!";

                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        public static DBConnection getInstance() {
            if (instance == null) {
                instance = new DBConnection();
            }
            return instance;
        }

        public Connection getConnection() {
            return connection;
        }

}
