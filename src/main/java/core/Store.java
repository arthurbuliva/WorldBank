package core;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Base64;
import java.util.HashMap;

/**
 * Safekeeping of stuff
 */
public final class Store
{
    // The database in which to store our coins
    private static final String VAULT = "jdbc:sqlite:silo/coins.db";

    public Store()
    {
        File vaultLocation = new File("silo");

        if (!vaultLocation.exists())
        {
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

    protected boolean saveCoin(String serialNumber, String value)
    {
        try (Connection connection = DriverManager.getConnection(VAULT))
        {
            // Idempotent
            String sql = "INSERT OR IGNORE INTO coins(serial_number, denomination) VALUES(?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, serialNumber);
            statement.setString(2, value);
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

    protected Object displayCoin(String serialNumber)
    {
        String coin = null;

        try (Connection connection = DriverManager.getConnection(VAULT))
        {
            String sql = "SELECT serial_number, denomination FROM coins WHERE serial_number = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, serialNumber);

            ResultSet resultSet = statement.executeQuery();

            // Extract data from result set
            while (resultSet.next())
            {

                // Retrieve by column name
                coin = resultSet.getString("denomination");
            }

            // Clean-up environment
            statement.close();
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }

        return coin;
    }

    private void constructStore()
    {
        try (Connection conn = DriverManager.getConnection(VAULT))
        {
            // SQL statement for creating a new table
            String sql = "CREATE TABLE coins(" +
                    "serial_number TEXT NOT NULL PRIMARY KEY," +
                    "denomination TEXT" +
                    ")";

            PreparedStatement statement = conn.prepareStatement(sql);

            // create a new table
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
