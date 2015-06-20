package com.fenrir;

import java.sql.Connection;
import java.sql.*;

/**
 * Created by Lars on 10-6-2015.
 */
public class DBConnect {

    String URL = "jdbc:mysql://localhost:3306/fenrir_customers";
    String user = "root";
//    String password = "1ExmAzjorjUZRoW3AMHA";
    String password = "";

    Connection conn;
    Statement stmt = null;
    ResultSet rs = null;

    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(ClassNotFoundException ex) {
            System.out.println("Error: unable to load driver class!");
            System.exit(1);
        }

        conn = DriverManager.getConnection(URL, user, password);
    }

    protected int getClientId(String clientUsername, String clientCompany) throws SQLException {
        int clientId = 0;
        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT id FROM users WHERE username LIKE '" + clientUsername + "' AND company LIKE '" + clientCompany + "'");
        rs = stmt.getResultSet();
        while (rs.next())
            clientId = rs.getInt(1);
        stmt.close();

        return clientId;
    }

    protected String getValueFromQuery(int clientId, String clientValue) throws SQLException {
        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT " + clientValue + " FROM users WHERE id LIKE '" + clientId + "'");
        rs = stmt.getResultSet();
        while (rs.next())
            clientValue = rs.getString(1);
        stmt.close();

        return clientValue;
    }
}
