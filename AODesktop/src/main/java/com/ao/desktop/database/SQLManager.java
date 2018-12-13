package com.ao.desktop.database;

import com.ao.desktop.data.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SQLManager {

    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mariadb://azelabs.net/attendance_overview";
    private static final String DB_USERNAME = "azewilous";
    private static final String DB_PASSWORD = "Lucariza";

    private boolean isConnected = false;
    private Connection conn = null;
    private String code = null;

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
        int rng = new Random().nextInt(1000);
        code = String.format("%04d", rng);
        String query = " INSERT INTO class_titles(title, code) VALUES(?, ?)";
        try {
            PreparedStatement prepStmt= conn.prepareStatement(query);
            prepStmt.setString(1, newSection);
            prepStmt.setString(2, code);
            prepStmt.execute();
            prepStmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<String[]> getClasses()
    {
        List<String[]> classNames = new ArrayList<>();
        String query = "SELECT * FROM class_titles";
        try {
            String[] classVals;
            PreparedStatement prepStmt =conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next())
            {
                classVals = new String[2];
                classVals[0] = rs.getString("title");
                classVals[1] = rs.getString("code");
                classNames.add(classVals);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classNames;
    }
    public boolean removeClass(String name)
    {
        String query = "Delete FROM class_titles WHERE title=?";
        try {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, name);
            prepStmt.execute();
            prepStmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<Student> getStudents()
    {
        List<Student> students = new ArrayList<>();
        Student fake = new Student("213454", "chris batrouni", "d237474e-2029-4b5f-8db4-84b3073bee41");
        students.add(fake);
        String query = "SELECT * FROM students";
        try {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                Student student = new Student(rs.getString("student_id"),
                        rs.getString("username"), rs.getString("device_unique_id"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    public String getCode() {
        return code;
    }
}