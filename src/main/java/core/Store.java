package core;

import java.io.File;
import java.sql.*;

/**
 * Safekeeping of the coins into the database
 */
final class Store
{
    // The database in which to store our coins
    private static final String VAULT = "jdbc:sqlite:silo/coins.db";

    /**
     * Initialize the Store
     */
    Store()
    {
        File vaultLocation = new File("silo");

        if (!vaultLocation.exists())
        {
            /*
             * Create the necessary folders to hold the SQLite database
             */
            vaultLocation.mkdirs();

            try
            {
                constructStore();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(0);
            }
        }
    }

    /**
     * Stores data into the database
     *
     * @param serialNumber The key with which to store the data
     * @param value        The actual value to be stored
     * @param country        The country associated with the value to be stored
     * @return True if successfully saved, false otherwise
     */
    boolean saveCoin(String serialNumber, String country, String value)
    {
        try (Connection connection = DriverManager.getConnection(VAULT))
        {
            // Idempotent
            String sql = "INSERT OR IGNORE INTO coins(serial_number, country, denomination) VALUES(?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, serialNumber);
            statement.setString(2, country);
            statement.setString(3, value);
            statement.executeUpdate();

            connection.close();

            return true;
        }
        catch (SQLException se)
        {
            se.printStackTrace();

            return false;
        }
    }

    /**
     * Extracts a given piece of data from the database
     *
     * @param serialNumber The id which we need to use to get the required data
     * @return The encrypted value as stored in the database
     */
    Object displayCoin(String serialNumber)
    {
        String coin = "";

        try (Connection connection = DriverManager.getConnection(VAULT))
        {
            String sql = "SELECT serial_number, denomination " +
                    "FROM coins " +
                    "WHERE serial_number = ? " +
                    "AND validity = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, serialNumber);
            statement.setInt(2, 1);

            ResultSet resultSet = statement.executeQuery();

            // Extract data from result set
            while (resultSet.next())
            {

                // Retrieve by column name
                coin = resultSet.getString("denomination");
            }

            // Clean-up environment
            connection.close();
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }

        return coin;
    }

    /**
     * Initialize the storage tables
     */
    private void constructStore()
    {
        try (Connection connection = DriverManager.getConnection(VAULT))
        {
            // SQL statement for creating a new table
            String sql = "CREATE TABLE coins(" +
                    "serial_number TEXT NOT NULL UNIQUE," +
                    "country TEXT," +
                    "denomination TEXT," +
                    "valid_from DATE DEFAULT CURRENT_DATE," +
                    "validity INT DEFAULT 1," +
                    "PRIMARY KEY (serial_number)" +
                    ")";

            PreparedStatement statement = connection.prepareStatement(sql);

            // create a new table
            statement.executeUpdate();

            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
