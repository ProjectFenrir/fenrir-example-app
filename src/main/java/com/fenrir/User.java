package com.fenrir;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.* ;

/**
 * Created by lars on 5/31/2015.
 */
public class User {
    DBConnect db;

    private int state = 1;
    private int id;
    private String username;
    private String password;
    private String email;
    private String company;
    private String clientDatabasePassword;
    private String clientPlainPassword;

    public User(int id, String password, DBConnect db) throws SQLException {
        this.db = db;
        this.id = id;
        this.username = db.getValueFromQuery(id, "username");
        this.clientPlainPassword = password;
        this.company = db.getValueFromQuery(id, "company");
        this.email = db.getValueFromQuery(id, "email");
    }

    public void verify() {
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
//            Create connection to the (json) url
            String jUrl = "http://188.166.123.191:8080/FenrirService/login?username=" + username + "&company=" + company;
            URL url = null;
            try {
                url = new URL(jUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

//                Get content from connection
                JsonParser parser = new JsonParser();
                JsonElement content = parser.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject contentObj = content.getAsJsonObject();
                setState(contentObj.get("state").getAsInt());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setState(1);
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
        for (int i = 0; i < byteData.length; i++)
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        clientHashPassword = sb.toString();

        return clientHashPassword;
    }

    protected String getUsername() {
        return username;
    }
    protected String getPassword() { return password; }
    protected String getEmail() { return email; }
    protected void setState(int state) {
        /*
        state 1 = unauthorized
        state 2 = verify
        state 3 = authorized
        */
        this.state = state;
    }
    protected int getState() { return state; }
}