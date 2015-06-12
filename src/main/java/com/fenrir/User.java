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
import java.sql.* ;

/**
 * Created by lars on 5/31/2015.
 */
public class User {
    DBConnect db = null;

    private int state;
    private String username;
    private String company;
    private String password;
    private String clientHashPassword;
    private String clientPassword;

    public User(String username, String company, String password) {
        this.username = username;
        this.company = company;
        this.password = password;
    }

    public void verify() {
        db = new DBConnect();
        try {
            db.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            clientHashPassword = db.getPassword(username, company);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            clientPassword = db.getHashPassword(username, company, this.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (clientHashPassword.equals(clientPassword)) {
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

    protected String getUsername() {
        return username;
    }
    protected String getPassword() { return clientPassword; }
    protected void setState(int state) {
        this.state = state;
    }
    protected int getState() { return state; }
}