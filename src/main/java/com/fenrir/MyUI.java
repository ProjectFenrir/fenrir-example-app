package com.fenrir;

import javax.servlet.annotation.WebServlet;

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

/**
 *
 */
@Theme("mytheme")
@Widgetset("com.fenrir.MyAppWidgetset")
public class MyUI extends UI {

    Navigator navigator;
    protected static final String MAINVIEW = "main";

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        getPage().setTitle("FENRIR login");

        // start login view
        loginView();
    }

    private void loginView() {
        final VerticalLayout layout = new VerticalLayout();
        layout.removeAllComponents();
        setContent(layout);

        Label logo = new Label("FENRIRsecurity");

        final TextField tfUsername = new TextField();
        tfUsername.setValue("Username");
        final PasswordField tfPassword = new PasswordField();
        tfPassword.setValue("Password");

        Button submit = new Button("Login");

        layout.addComponent(logo);
        layout.addComponent(tfUsername);
        layout.addComponent(tfPassword);
        layout.addComponent(submit);

        submit.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (tfUsername.getValue().equals("admin") && tfPassword.getValue().equals("password")) {
                    authenticateView(layout, new String(tfUsername.getValue()));
                }
            }
        });
    }

    private void authenticateView(final VerticalLayout layout, final String username) {
        layout.removeAllComponents();

        ProgressBar waitingVerify = new ProgressBar();
        waitingVerify.setIndeterminate(true);

        Button btnSkip = new Button("skip");
        Button btnCancel = new Button("cancel");

        layout.addComponent(waitingVerify);
        layout.addComponent(btnSkip);
        layout.addComponent(btnCancel);

        btnSkip.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                mainView(layout, new String(username));
            }
        });
        btnCancel.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                loginView();
            }
        });
    }

    private void mainView(final VerticalLayout layout, final String username) {
        layout.removeAllComponents();

        HorizontalLayout menuView = new HorizontalLayout();
        menuView.setSizeFull();
        layout.addComponent(menuView);

        Label greetUser = new Label("Hello, " + username + "!");

        Button btnEdit = new Button("edit user");
        Button btnLogout = new Button("logout user");

        layout.addComponent(greetUser);
        menuView.addComponent(btnEdit);
        menuView.addComponent(btnLogout);

        btnLogout.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                loginView();
            }
        });
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
