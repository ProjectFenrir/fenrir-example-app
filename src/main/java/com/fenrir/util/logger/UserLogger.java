package com.fenrir.util.logger;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by lars on 6/18/2015.
 */
public class UserLogger {

    private final static Logger LOGGER = Logger.getLogger(UserLogger.class.getName());
    private static FileHandler fh = null;
    private static XMLFormatter formatXml;

    private static String username;

    public static void init() throws IOException {
        try {
            fh = new FileHandler("loginLogger.log", false);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        formatXml = new XMLFormatter();
        fh.setFormatter(formatXml);
        LOGGER.addHandler(fh);
    }

    public static void logVerification(String clientUsername, boolean autorised) {
        username = clientUsername;
        if (autorised)
            LOGGER.log(Level.INFO, username + " -- success");
        else
            LOGGER.log(Level.WARNING, username + " -- failed!");
    }

    public static void logUnauthorizedVisit() {
        LOGGER.log(Level.WARNING, "Anonymous user navigated to the dashboard (access denied)");
    }

    public static void logAction(String item) {
        LOGGER.log(Level.INFO, username + " currently viewing -- " + item);
    }
}