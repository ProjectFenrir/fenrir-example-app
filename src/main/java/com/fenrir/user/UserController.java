package com.fenrir.user;

import java.sql.SQLException;

/**
 * Created by lars on 6/22/2015.
 */
public class UserController {

    private UserModel model;
    private UserView view;

    public UserController(UserModel model, UserView view) {
        this.model = model;
        this.view = view;
    }

    public void setUserName(String username) {
        model.setUsername(username);
    }
    public String getUserName() { return model.getUsername(); }

    public void setUserEmail(String email) throws SQLException {
        model.setEmail(email);
    }
    public String getUserEmail() { return model.getEmail(); }

    public void setUserDatabasePassword(String databasePassword) throws SQLException {
        model.setDatabasePassword(databasePassword);
    }
    public String getUserDatabasePassword() { return model.getDatabasePassword(); }

    public void setState(int state) {
        model.setState(state);
    }
    public int getState() { return model.getState(); }

    public void updateView() {
        view.printWelcomeMessage(model.getUsername(), model.getCompany(), model.getEmail());
    }

    public void setUserId(int id) {
        model.setId(id);
    }
    public int getUserId() { return model.getId(); }
}
