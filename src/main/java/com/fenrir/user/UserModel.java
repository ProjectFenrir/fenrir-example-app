package com.fenrir.user;

import com.fenrir.database.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;

/**
 * Created by lars on 6/22/2015.
 */
public class UserModel {
    DatabaseConnection db;

    private int state = 1;
    private int id;
    private String username;
    private String email;
    private String password;
    private String clientPlainPassword;
    private String clientDatabasePassword;

    public UserModel(int id, String password, DatabaseConnection db) throws SQLException {
        this.db = db;
        this.id = id;
        this.username = db.getValueFromQuery(id, "username");
        this.clientPlainPassword = password;
        this.email = db.getValueFromQuery(id, "email");
    }

    public boolean passwordIsCorrect() {
        try {
            clientDatabasePassword = db.getValueFromQuery(id, "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            password = calculateHash(id, clientPlainPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (clientDatabasePassword.equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    protected String calculateHash(int clientId, String clientPassword) throws SQLException {
        String clientSalt = db.getValueFromQuery(clientId, "salt");
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
        for (int i = 0; i < byteData.length; i++) {
            System.out.println(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        clientHashPassword = sb.toString();

        return clientHashPassword;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public void setState(int state) {
        /*
        state 1 = unauthorized
        state 2 = passwordIsCorrect
        state 3 = authorized
        */
        this.state = state;
    }
    public int getState() { return state; }
}
