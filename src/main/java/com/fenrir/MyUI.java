package com.fenrir;

import javax.servlet.annotation.WebServlet;

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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.declarative.Design;

/**
 *
 */
@Theme("mytheme")
@Widgetset("com.fenrir.MyAppWidgetset")
public class MyUI extends UI {

    Navigator navigator;

    protected static final String LOGINVIEW = "login";
    protected static final String MAINVIEW = "main";
    protected String username = "";
    public boolean authenticated = false;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("FENRIR login");

        // navigator
        navigator = new Navigator(this, this);

        // register views
        navigator.addView(LOGINVIEW, new LoginView());
        navigator.addView(MAINVIEW, new MainView());
    }

    public class LoginView extends VerticalLayout implements View {

        public LoginView() {
            setSizeFull();

            Label logo = new Label("FENRIRsecurity");

            final TextField tfUsername = new TextField();
            tfUsername.setValue("Username");
            final PasswordField tfPassword = new PasswordField();
            tfPassword.setValue("Password");

            Button submit = new Button("Login",
                    new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            if (tfUsername.getValue().equals("admin") && tfPassword.getValue().equals("password")) {
                                username = tfUsername.getValue();
                                authenticated = true;
                                navigator.navigateTo(MAINVIEW);
                            }
                        }
                    });

            addComponent(logo);
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
                    authenticated = false;
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
            if (event.getParameters() == null || event.getParameters().isEmpty() && authenticated) {
                equalPanel.setContent(new Label("Hello, " + username));
                return;
            } else
                navigator.navigateTo(LOGINVIEW);
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
