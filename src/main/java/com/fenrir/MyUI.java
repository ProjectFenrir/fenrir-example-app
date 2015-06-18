package com.fenrir;

import javax.servlet.annotation.WebServlet;

import com.fenrir.util.logger.LoginLogger;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;

import java.io.IOException;

/**
 * Created by Lars Hoevenaar
 *
 */
@Theme("mytheme")
@Widgetset("com.fenrir.MyAppWidgetset")
public class MyUI extends UI {

    Navigator navigator;
    User user;
    LoginLogger log = null;

    protected static final String LOGINVIEW = "login";
    protected static final String MAINVIEW = "main";

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("FENRIR login");

//        Create loggin session
        log = new LoginLogger();

        navigator = new Navigator(this, this);
//        Register views
        navigator.addView(LOGINVIEW, new LoginView());
        navigator.addView(MAINVIEW, new MainView());
    }

    public class LoginView extends VerticalLayout implements View {

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
                                log.logUser(tfUsername.getValue(), true);
                                navigator.navigateTo(MAINVIEW);
                            } else {
                                log.logUser(tfUsername.getValue(), false);
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

    @DesignRoot
    public class MainView extends VerticalLayout implements View {
        class ButtonListener implements Button.ClickListener {
            String menuItem;
            public ButtonListener(String menuItem) {
                this.menuItem = menuItem;
            }

            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(MAINVIEW + "/" + menuItem);
            }
        }

        VerticalLayout menuContent;
        Panel equalPanel;
        Button logout;

        public MainView() {
            Design.read(this);

            menuContent.addComponent(new Button("Edit profile",
                    new ButtonListener("editProfile")));
            menuContent.addComponent(new Button("List Users",
                    new ButtonListener("listUsers")));

            logout.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    user.setState(1);
                    navigator.navigateTo(LOGINVIEW);
                }
            });
        }

        @DesignRoot
        class ProfileView extends VerticalLayout {
            Label watching;

            public ProfileView(String item) {
                Design.read(this);
                watching.setValue("Viewing page: " + item);
            }
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
//            If no user session is found; redirect to login
            if (user == null)
                navigator.navigateTo(LOGINVIEW);

            if (user != null) {
//            If user session is found, but not authorised; redirect to login
                if (user.getState() != 2) {
                    navigator.navigateTo(LOGINVIEW);
//            If authorised; grant access and redirect to main
                } else {
                    if (event.getParameters() == null || event.getParameters().isEmpty()) {
                        equalPanel.setContent(new Label("Hello, " + user.getUsername()));
                        return;
                    } else
                        equalPanel.setContent(new ProfileView(event.getParameters()));
                }
            }
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
