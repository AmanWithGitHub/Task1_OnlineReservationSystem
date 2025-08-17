import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {

    public static void main(String[] args) {
        try {
            // Try to get a connection
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println("Success! Connection to the database established.");
                // Don't forget to close the connection
                connection.close();
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }
}