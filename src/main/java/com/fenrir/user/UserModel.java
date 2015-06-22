package com.fenrir.user;

import com.fenrir.database.DatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Created by lars on 6/22/2015.
 */
public class UserModel {
    DatabaseConnection db;

    private int state = 1;
    private int id;
    private String username;
    private String company;
    private String email;
    private String password;
    private String plainPassword;
    private String databasePassword;

    public UserModel(DatabaseConnection db) {
        this.db = db;
    }

    public boolean passwordIsCorrect() throws SQLException {
        password = calculateHash(id, plainPassword);

        if (databasePassword.equals(password)) {
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
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        clientHashPassword = sb.toString();

        return clientHashPassword;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() { return id; }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setState(int state) {
        /*
        state 1 = unauthorized
        state 2 = passwordIsCorrect
        state 3 = authorized
        */
        this.state = state;
    }
    public int getState() { return state; }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }
    public String getPlainPassword() { return plainPassword; }

    public void setEmail(String email) throws SQLException {
        this.email = db.getValueFromQuery(id, email);
    }
    public String getEmail() { return email; }

    public void setDatabasePassword(String password) throws SQLException {
        this.databasePassword = db.getValueFromQuery(id, password);
    }
    public String getDatabasePassword() { return databasePassword; }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getCompany() { return company; }

}
