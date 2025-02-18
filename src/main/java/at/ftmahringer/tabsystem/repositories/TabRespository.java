package at.ftmahringer.tabsystem.repositories;

import at.ftmahringer.tabsystem.model.Tab;
import at.ftmahringer.tabsystem.utils.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TabRespository {

    private Connection connection;
    private static TabRespository instance = null;
    private List<Tab> tasks;

    private TabRespository() {
        tasks = new ArrayList<>();
        connection = DBConnection.getInstance();
        // check for table existence and create it if it does not exist
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tabs (id INT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(255), icon VARCHAR(255), priority_order INT, is_active BOOLEAN)");
            ResultSet rs = stmt.executeQuery("SELECT * FROM tabs");
            while (rs.next()) {
                System.out.println(rs.getString("title"));
                tasks.add(new Tab(rs.getString("title"), rs.getString("icon"), rs.getInt("priority_order"), rs.getBoolean("is_active")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static TabRespository getInstance() {
        if (instance == null) {
            instance = new TabRespository();
        }
        return instance;
    }

    public List<Tab> findAllActive() {
        tasks.clear();
        getTasksWithQuery("SELECT * FROM tabs WHERE is_active = true ORDER BY priority_order");
        return tasks;
    }

    private List<Tab> getTasksWithQuery(String query) {
        tasks.clear();
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                tasks.add(new Tab(rs.getString("title"), rs.getString("icon"), rs.getInt("priority_order"), rs.getBoolean("is_active")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
