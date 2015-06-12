package com.fenrir;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    Connection conn = null;
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

    protected String getPassword(String clientUsername, String clientCompany) throws SQLException {
        String clientHashPassword = "";
        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT password FROM users WHERE username LIKE '" + clientUsername + "' AND company LIKE '" + clientCompany + "'");
        rs = stmt.getResultSet();
        while (rs.next())
            clientHashPassword = rs.getString(1);
        stmt.close();

        return clientHashPassword;
    }

    protected String getSalt(String clientUsername, String clientCompany) throws SQLException {
        String clientSalt = "";
        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT salt FROM users WHERE username LIKE '" + clientUsername + "' AND company LIKE '" + clientCompany + "'");
        rs = stmt.getResultSet();
        while (rs.next())
            clientSalt = rs.getString(1);
        stmt.close();

        return clientSalt;
    }

    protected String getHashPassword(String clientUsername, String clientCompany, String clientPassword) throws SQLException {
        String clientSalt = getSalt(clientUsername, clientCompany);
        String clientHashPassword = clientPassword + clientSalt;

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(clientHashPassword.getBytes());

        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++)
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        clientHashPassword = sb.toString();

        return clientHashPassword;
    }
}