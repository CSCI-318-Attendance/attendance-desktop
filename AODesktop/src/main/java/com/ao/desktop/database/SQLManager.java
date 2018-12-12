package com.ao.desktop.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mariadb://azelabs.net/attendance_overview";
    private static final String DB_USERNAME = "azewilous";
    private static final String DB_PASSWORD = "Lucariza";

    private boolean isConnected = false;
    private Connection conn = null;

    public SQLManager() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Registered jdbc driver.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Could not locate jdbc driver: " + ex);
            return;
        }
        System.out.println("Initiating connection to database.");
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            isConnected = true;
            System.out.println("You have successfully connected the database.");
        } catch (SQLException ex) {
            System.out.println("Could not create connection: " + ex);
        }
    }

    public boolean isInitialize() {
        return isConnected && conn != null;
    }

    public boolean login(String user, String pass) {
        String query = "SELECT * FROM teachers";

        try {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                String store_user = rs.getString("username");
                String store_pass = rs.getString("password");
                if (user.equals(store_user) && pass.equals(store_pass)) {
                    System.out.println("Found correct login");
                    return true;
                }
            }
            System.out.println("Could not find login");
        } catch (SQLException ex) {
            System.out.println("There was an error when logging in: " + ex);
        }
        return false;
    }
    public boolean saveClass(String newSection)
    {
        String query = " INSERT INTO class_titles(title) VALUES(?)";
        try {
            PreparedStatement prepStmt= conn.prepareStatement(query);
            prepStmt.setString(1, newSection);
            prepStmt.execute();
            prepStmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<String> getClasses()
    {
        List<String> classNames = new ArrayList<>();
        String query = "SELECT * FROM class_titles";
        try {
            PreparedStatement prepStmt =conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next())
            {
                classNames.add(rs.getString("title"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classNames;
    }
    public boolean removeClass(String name)
    {
        String query = "Delete * FROM class_titles WHERE title=?";
        try {
            PreparedStatement prepStmt =conn.prepareStatement(query);
            prepStmt.setString(1,name);
            prepStmt.execute();
            prepStmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getStudent(String id)
    {
        String name="";
        String query = "SELECT * FROM students";
        try {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            name =rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
}