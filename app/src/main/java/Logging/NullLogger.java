package Logging;

public class NullLogger implements Logging {
    @Override
    public void info(String format, Object... args) {

    }

    @Override
    public void warning(String format, Object... args) {

    }

    @Override
    public void error(String format, Object... args) {

    }

    @Override
    public void error(Exception e, Object... args) {

    }
}
