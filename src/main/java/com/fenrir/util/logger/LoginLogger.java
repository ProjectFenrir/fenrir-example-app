package com.fenrir.util.logger;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by lars on 6/18/2015.
 */
public class LoginLogger {

    private final static Logger LOGGER = Logger.getLogger(LoginLogger.class.getName());
    private static FileHandler fh = null;
    private static XMLFormatter formatXml;

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

    public static void logUser(String username, boolean autorised) {
        if (autorised)
            LOGGER.log(Level.INFO, username + " -- success");
        else
            LOGGER.log(Level.WARNING, username + " -- failed!");
    }
}
