package com.fenrir.view;

import com.fenrir.User;
import com.fenrir.util.logger.UserLogger;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 * Created by Lars on 19-6-2015.
 */
@DesignRoot
public class MainView extends VerticalLayout implements View {

    Navigator navigator;
    User user;
    UserLogger log;

    protected static final String LOGINVIEW = "login";
    protected static final String MAINVIEW = "main";

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
            log.logAction(item);
            watching.setValue("Viewing page: " + item);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
//            If no user session is found; redirect to login
        if (user == null) {
            log.logUnauthorizedVisit();
            navigator.navigateTo(LOGINVIEW);
        }

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
