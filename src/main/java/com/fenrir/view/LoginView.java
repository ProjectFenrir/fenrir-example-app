package com.fenrir.view;

import com.fenrir.User;
import com.fenrir.util.logger.UserLogger;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;

import java.io.IOException;

/**
 * Created by Lars on 19-6-2015.
 */
public class LoginView extends VerticalLayout implements View {

    Navigator navigator;
    User user;
    UserLogger log;

    protected static final String LOGINVIEW = "login";
    protected static final String MAINVIEW = "main";

    public LoginView() {
        setSizeFull();

        Label logo = new Label("FENRIRsecurity");

        final TextField tfCompany = new TextField();
        tfCompany.setValue("Company");
        final TextField tfUsername = new TextField();
        tfUsername.setValue("Username");
        final PasswordField tfPassword = new PasswordField();
        tfPassword.setValue("Password");

        Button submit = new Button("Login",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        try {
                            log.init();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                            Verify user credentials
                        user = new User(tfUsername.getValue(), tfCompany.getValue(), tfPassword.getValue());
                        user.verify();
                        tfPassword.setValue(user.getPassword());
//                            If state (=2); grant access
                        if (user.getState() == 2) {
                            log.logVerification(tfUsername.getValue(), true);
                            navigator.navigateTo(MAINVIEW);
                        } else {
                            log.logVerification(tfUsername.getValue(), false);
                            Notification.show("Incorrect credentials");
                        }
                    }
                });

        addComponent(logo);
        addComponent(tfCompany);
        addComponent(tfUsername);
        addComponent(tfPassword);
        addComponent(submit);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Notification.show("FENRIR secured");
    }
}
