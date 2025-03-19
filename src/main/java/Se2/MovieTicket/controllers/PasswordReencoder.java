package Se2.MovieTicket.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.*;

public class PasswordReencoder {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/cinema%20db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String TABLE_NAME = "users";
    private static final String ID_COLUMN = "user_id";
    private static final String PASSWORD_COLUMN = "password";

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            // Step 1: Fetch all user passwords
            String selectQuery = "SELECT " + ID_COLUMN + ", " + PASSWORD_COLUMN + " FROM " + TABLE_NAME;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                 ResultSet rs = selectStmt.executeQuery()) {

                while (rs.next()) {
                    int userId = rs.getInt(ID_COLUMN);
                    String plainPassword = rs.getString(PASSWORD_COLUMN);
                    String encodedPassword = encoder.encode(plainPassword);

                    // Step 2: Update the password with encoded version
                    String updateQuery = "UPDATE " + TABLE_NAME + " SET " + PASSWORD_COLUMN + " = ? WHERE " + ID_COLUMN + " = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, encodedPassword);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                        System.out.println("Updated password for user ID: " + userId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

