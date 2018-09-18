package test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;

public class Workflow
{
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    //    static final String DB_URL = "jdbc:hsqldb:file:db/workflow_database";
//    static final String DB_URL = "jdbc:hsqldb:mem:db_file";
    private static final String DATABASE = "jdbc:sqlite:db/workflow_database";

    // Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    public static void main(String[] args) throws Exception
    {
        // encode
        byte[] encodedBytes = Base64.getEncoder().encode("My name is Arthur Buliva".getBytes(StandardCharsets.UTF_8));
        String encoded = new String(encodedBytes, Charset.forName("UTF-8"));

        byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
        String decoded = new String(decodedBytes, Charset.forName("UTF-8"));



        System.out.println(encoded);
        System.out.println(decoded);


        Connection conn = null;
        Statement stmt = null;
        try
        {
            // Open a connection
            File folder = new File("db");
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DATABASE);

            // Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, first, last, age FROM Employees";
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Employees VALUES(1,?,'Smith', 100)");
//            stmt.executeUpdate("CREATE TABLE Employees ( id INTEGER IDENTITY, first VARCHAR(256),  last VARCHAR(256),age INTEGER)");
            statement.setString(1, encoded);
            statement.executeUpdate();

            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next())
            {
                // Retrieve by column name
                int id = rs.getInt("id");
                int age = rs.getInt("age");
                String first = rs.getString("first");
                String last = rs.getString("last");

                System.out.print("ID: " + id);
                System.out.print(", Age: " + age);
                System.out.print(", First: " + first);
                System.out.println(", Last: " + last);
            }
            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // finally block used to close resources
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (SQLException se2)
            {
            }
            try
            {
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
