package org.logging;

import android.util.Log;

import org.logging.Logging;

public class LogcatLogger implements Logging {
    @Override
    public void info(String format, Object... args) {
        Log.i(INFO_TAG, String.format(format, args));
    }

    @Override
    public void warning(String format, Object... args) {
        Log.w(WARNING_TAG, String.format(format, args));
    }

    @Override
    public void error(String format, Object... args) {
        Log.e(ERROR_TAG, String.format(format, args));
    }

    @Override
    public void error(Exception e, Object... args) {
        this.error(e.getMessage());
        for (StackTraceElement se : e.getStackTrace()) {
            this.error("File: %s, Method: %s, Line: %s", se.getFileName(), se.getMethodName(), se.getLineNumber());
        }
    }
}
