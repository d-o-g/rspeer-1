package org.rspeer.api_services;

public class Logger {

    private static Logger instance;

    public static Logger getInstance() {
        if(instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private Logger() {
    }

    public void capture(Throwable e) {
        if (e.getMessage().contains("Connection pool shut down")) {
            return;
        }
        String debug = System.getenv("rspeer_debug");
        if(debug == null || !debug.equals("true")) {
            return;
        }
        e.printStackTrace();
    }

    public void capture(String message) {
        System.out.println(message);
    }
}
