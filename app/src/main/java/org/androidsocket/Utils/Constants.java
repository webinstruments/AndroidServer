package org.androidsocket;

public final class Constants {
    public static final String START_SERVICE = "START";
    public static final String ACTION_MAIN = "SocketService";
    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String CHANNEL_DESCRIPTION = "AndroidSocket";
    public static final String SERVER_PORT = "SERVER_PORT";
    public static final String POLLING_INTERVAL = "POLLING_INTERVAL";
    public static final String CONNECTION_DETAIL_INTENT_KEY = "ConnectionData";
    public static final short CONNECTION_BAD_THRESHOLD = 5;
    public static final short CONNECTION_MEDIUM_THRESHOLD = 12;
    public static final short CONNECTION_GOOD_THRESHOLD = 20;
    public static final short CONNECTION_BEST_THRESHOLD = 30;
}
