package fr.mana.terrabank.loading;

import fr.mana.terrabank.*;
import org.jdbi.v3.core.Jdbi;

import java.sql.*;

public class DatabaseManager {

    private final TerraBank main;
    private Connection connection;

    public DatabaseManager(TerraBank main) {
        this.main = main;
    }

    public void connect() {
        String host = main.getConfig().getString("database.host");
        int port = main.getConfig().getInt("database.port");
        String database = main.getConfig().getString("database.database");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        String username = main.getConfig().getString("database.user");
        String password = main.getConfig().getString("database.password");

        assert username != null;
        assert password != null;

        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password);

        jdbi.useHandle(handle -> {
            // Connection test
            handle.execute("SELECT 1");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println("   TerraBank");
            System.out.println("Connected successfully !");
            System.out.println("   TerraBank");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
        });
    }
    public Connection getConnection() throws SQLException {
        String host = main.getConfig().getString("database.host");
        int port = main.getConfig().getInt("database.port");
        String database = main.getConfig().getString("database.database");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        String username = main.getConfig().getString("database.user");
        String password = main.getConfig().getString("database.password");

        assert username != null;
        assert password != null;

        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password);

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
