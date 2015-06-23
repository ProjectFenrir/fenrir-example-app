package com.fenrir.user;

/**
 * Created by lars on 6/22/2015.
 */
public class UserView {

    public void printWelcomeMessage(String userName, String userCompany, String userEmail) {
        System.out.println("Hello " + userName + " (" + userEmail + ") from " + userCompany);
    }
}
