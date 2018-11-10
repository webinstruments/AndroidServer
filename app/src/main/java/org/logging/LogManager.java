package org.logging;

import java.util.logging.Logger;

public class LogManager {
    private LogManager() {
        this.currentLogger = new NullLogger();
    }

    public static Logging getLogger() {
        if(singleton == null) {
            singleton = new LogManager();
        }

        return singleton.currentLogger;
    }

    synchronized public static void setLogger(Logging l) {
        if(singleton == null) {
            singleton = new LogManager();
        }
        singleton.currentLogger = l;
    }

    private Logging currentLogger;
    private static LogManager singleton = null;
}
