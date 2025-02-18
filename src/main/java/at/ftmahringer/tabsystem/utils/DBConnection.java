package at.ftmahringer.tabsystem.utils;

import at.ftmahringer.tabsystem.Starter;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Connection instance = null;
    //private Connection conn;

    private DBConnection() {
        try {
            Properties props = new Properties();
            props.load(new FileReader(Starter.getRessource("config.properties")));
            String jdbcUrl = props.getProperty("connection_string"); // "jdbc:mariadb://localhost:4306/taskmanagement?user=taskmanagement&password=taskmanagement"
            instance = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new DBConnection();
        }
        return instance;
    }

    public static void closeConnection() {
        try {
            instance.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
