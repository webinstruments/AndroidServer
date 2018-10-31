package Logging;

import android.util.Log;

import Logging.Logging;

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
        error(ERROR_TAG, e.getMessage());
        for (StackTraceElement se : e.getStackTrace()) {
            error(ERROR_TAG, String.format("File: %s, Method: %s, Line: %s", se.getFileName(), se.getMethodName(), se.getLineNumber()));
        }
    }
}
