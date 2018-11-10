package org.androidsocket;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;

public class WSUtils {
    public static String getStreamFromWS(WebSocket ws) {
        return ws.getLocalSocketAddress().toString() + ", " + ws.getRemoteSocketAddress().toString();
    }

    public static Notification buildNotification(Context c, String title, String ticker, String text, int iconResource, PendingIntent contentPending, boolean onGoing) {
        Bitmap icon = BitmapFactory.decodeResource(c.getResources(), iconResource);
        Notification result = new NotificationCompat.Builder(c, Constants.CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(ticker)
                .setContentText(text)
                .setSmallIcon(iconResource)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(contentPending)
                .setOngoing(onGoing)
                .build();

        result.flags = result.flags | Notification.FLAG_NO_CLEAR;

        return result;
    }
}
