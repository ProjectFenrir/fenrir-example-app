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

/**
 * Created by lars on 5/31/2015.
 */
public class User {

    private int state;
    private String username;
    private String company;

    public User(String username, String company) {
        this.username = username;
        this.company = company;
    }

    public void verify() {
        String jUrl = "http://188.166.123.191:8080/FenrirService/login?username=" + username + "&company=" + company;

        // create connection to the (json) url
        URL url = null;
        String state = "";
        try {
            url = new URL(jUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection request = (HttpURLConnection)url.openConnection();
            request.connect();

            // get content from connection
            JsonParser parser = new JsonParser();
            JsonElement content = parser.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject contentObj = content.getAsJsonObject();
            setState(contentObj.get("state").getAsInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getUsername() {
        return username;
    }

    protected void setState(int state) {
        this.state = state;
    }

    protected int getState() {
        return state;
    }
}
