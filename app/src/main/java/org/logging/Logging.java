package org.logging;

public interface Logging {
    String INFO_TAG = "AS_INFO";
    String WARNING_TAG = "AS_WARNING";
    String ERROR_TAG = "AS_ERROR";

    public void info(String format, Object ...args);

    public void warning(String format, Object ...args);

    public void error(String format, Object ...args);

    public void error(Exception e, Object ...args);

}
