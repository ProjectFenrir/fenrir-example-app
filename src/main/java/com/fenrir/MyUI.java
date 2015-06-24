package com.fenrir;

import javax.servlet.annotation.WebServlet;

import com.fenrir.database.DatabaseConnection;
import com.fenrir.mailer.SendVerificationEmail;
import com.fenrir.user.UserController;
import com.fenrir.user.UserModel;
import com.fenrir.user.UserView;
import com.fenrir.util.logger.UserLogger;
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
import java.security.SecureRandom;
import java.sql.SQLException;

/**
 * Created by Lars Hoevenaar
 *
 */
@Theme("mytheme")
@Widgetset("com.fenrir.MyAppWidgetset")
public class MyUI extends UI {

    Navigator navigator;

    UserController userController;
    UserModel userModel;
    UserView userView;

    DatabaseConnection db = null;
    UserLogger log = null;

    SecureRandom random;
    Integer token;

    protected static final String LOGINVIEW = "login";
    protected static final String VERIFICATIONVIEW = "verification";
    protected static final String MAINVIEW = "main";

    protected static final String TF_COMPANY = "Company";
    protected static final String TF_USERNAME = "Username";
    protected static final String TF_PASSWORD = "Password";

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("FENRIR login");

//        Create loggin session
        log.getInstance();

        navigator = new Navigator(this, this);
//        Register views
        navigator.addView(LOGINVIEW, new LoginView());
        navigator.addView(VERIFICATIONVIEW, new VerificationView());
        navigator.addView(MAINVIEW, new MainView());
        navigator.navigateTo(LOGINVIEW);
    }

    public class LoginView extends VerticalLayout implements View {

        private String company, username, password;

        public LoginView() {
            Label logo = new Label("FENRIRsecurity");

            final TextField tfCompany = new TextField();
            tfCompany.setValue(TF_COMPANY);
            final TextField tfUsername = new TextField();
            tfUsername.setValue(TF_USERNAME);
            final PasswordField tfPassword = new PasswordField();
            tfPassword.setValue(TF_PASSWORD);

            Button submit = new Button("Login",
                    new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            try {
                                log.getInstance().init();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            company = tfCompany.getValue();
                            username = tfUsername.getValue();
                            password = tfPassword.getValue();

//                            Get user id & email
                            db = new DatabaseConnection();
                            int clientId = 0;
                            try {
                                db.connect();
                                clientId = db.getClientId(username, company);
                                if (clientId != 0) {
//                                    Generate 5 numeric length token
                                    random = new SecureRandom();
                                    token = random.nextInt(99999 - 10000 + 1) + 10000;

//                                    Verify user credentials
//                                    userModel = new UserModel(clientId, tfPassword.getValue(), db);
                                    userModel = createUserSession();
                                    userView = new UserView();
                                    userController = new UserController(userModel, userView);

//                                    userController.setUserPlainPassword(password);
                                    userController.setUserId(clientId);
                                    userController.setUserDatabasePassword("password");

                                    userModel.passwordIsCorrect();
                                    tfPassword.setValue(userController.getUserDatabasePassword());

//                                    If state (=2); grant access
                                    if (userModel.passwordIsCorrect() == true) {
                                        log.getInstance().logAutorisation(username, true);
                                        log.getInstance().logInfo(userController.getUserDatabasePassword());

                                        SendVerificationEmail mail = new SendVerificationEmail();

                                        userController.setUserEmail("email");
                                        mail.sendEmail(userController.getUserName(), userController.getUserEmail(), token);

                                        navigator.navigateTo(VERIFICATIONVIEW);
                                    } else {
                                        log.getInstance().logAutorisation(username, false);
                                        Notification.show("Incorrect credentials");
                                    }
                                    db.conn.close();

//                                    Set field values to default, forget user input
                                    tfCompany.setValue("Company");
                                    tfUsername.setValue("Username");
                                    tfPassword.setValue("Password");
                                } else {
                                    log.getInstance().logAutorisation("@unknownuser@", false);
                                    Notification.show("Unknown user");
                                }
                            } catch (SQLException e) {
                                log.getInstance().logInfo(e.getMessage());
                            }
                        }
                    });

            logo.setId("label-logo");
            tfCompany.setId("field-company");
            tfUsername.setId("field-username");
            tfPassword.setId("field-password");
            submit.setId("button-login");

            addComponent(logo);
            addComponent(tfCompany);
            addComponent(tfUsername);
            addComponent(tfPassword);
            addComponent(submit);
        }

        private UserModel createUserSession() throws SQLException {
            UserModel user = new UserModel(db);
            user.setCompany(company);
            user.setUsername(username);
            user.setPlainPassword(password);
            return user;
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            Notification.show("FENRIR secured");
        }
    }

    @DesignRoot
    public class VerificationView extends VerticalLayout implements View {

        int incorrectTokenEntry = 0;

        public VerificationView() {
            final TextField tfToken = new TextField();
            tfToken.setValue("Token");

            Button submit = new Button("Okay",
                    new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            if (tfToken.getValue().equals(token.toString())) {
                                log.logVerification(userController.getUserName(), tfToken.getValue(), true);
                                userController.setState(2);
                                navigator.navigateTo(MAINVIEW);
                            } else {
                                incorrectTokenEntry++;
                                if (incorrectTokenEntry > 2) {
                                    log.logVerification(userController.getUserName(), tfToken.getValue(), false);
                                    navigator.navigateTo(LOGINVIEW);
                                }
                            }
                            tfToken.setValue("token");
                        }
                    });

            tfToken.setId("label-token");
            submit.setId("button-login");

            addComponent(tfToken);
            addComponent(submit);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
//            If no user session is found; redirect to login
            if (userModel == null) {
                log.getInstance().logUnauthorizedVisit();
                navigator.navigateTo(LOGINVIEW);
            }

            if (userModel != null) {
//            If user session is found, but not authorised; redirect to login
                try {
                    if (userModel.passwordIsCorrect() != true)
                        navigator.navigateTo(LOGINVIEW);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Requesting token");
            }
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
                    userModel.setState(1);
                    navigator.navigateTo(LOGINVIEW);
                }
            });
        }

        @DesignRoot
        class ProfileView extends VerticalLayout {
            Label watching;

            public ProfileView(String item) {
                Design.read(this);
                log.getInstance().logAction(item);
                watching.setValue("Viewing page: " + item);
            }
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
//            If no user session is found; redirect to login
            if (userModel == null) {
                log.getInstance().logUnauthorizedVisit();
                navigator.navigateTo(LOGINVIEW);
            }

            if (userModel != null) {
//            If user session is found, but not authorised; redirect to login
                if (userController.getState() != 2) {
                    navigator.navigateTo(LOGINVIEW);
//            If authorised; grant access and redirect to main
                } else {
                    if (event.getParameters() == null || event.getParameters().isEmpty()) {
                        userController.updateView();
                        equalPanel.setContent(new Label("Hello " + userController.getUserName()));
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
