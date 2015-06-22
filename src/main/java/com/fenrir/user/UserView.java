package com.fenrir.user;

/**
 * Created by lars on 6/22/2015.
 */
public class UserView {

    public void printUserDetails(String userName, String userCompany, String userEmail) {
        System.out.println("User:\t\t " + userName);
        System.out.println("Company:\t " + userCompany);
        System.out.println("Email:\t\t " + userEmail);
    }
}
