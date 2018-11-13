package org.androidsocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import org.logging.LogManager;

import java.util.UUID;

public class WebSocketService extends Service {
    private final IBinder binder = new ASBinder();
    private final String ID = UUID.randomUUID().toString();
    private static boolean isStarted = false;
    private Server server;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;

    public boolean isStarted() {
        return isStarted;
    }

    public String getID() {
        return ID;
    }

    public void setTimeout(int timeout) {
        if (this.server != null) {
            this.server.setTimeout(timeout);
        }
    }

    public void stopService() {
        this.stop();
        this.stopForeground(true);
        this.stopSelf();
    }

    public int getTimeout() {
        return this.server.getTimeout();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.setNotification();
        return Service.START_STICKY;
    }

    public void start(int port, int interval) {
        if (this.server == null) {
            this.server = new Server(port, interval);
            this.server.setReuseAddr(true);
            this.server.start();
        }
        if (!isStarted()) {
            isStarted = true;
            this.setNotification();
        }
    }

    public void stop() {
        if (this.server != null) {
            this.server.stop();
            this.server = null;
        }
        if (isStarted()) {
            isStarted = false;
            this.setNotification();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogManager.getLogger().info("Intent %s connected", intent.getPackage());
        return binder;
    }

    private void setNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(Constants.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String title = getResources().getString(R.string.app_name);
        String contextText = this.isStarted() ? getResources().getString(R.string.state_started) : getResources().getString(R.string.state_stopped);
        LogManager.getLogger().info("Building Notification...");
        Notification notification = WSUtils.buildNotification(
                this, title, title, contextText, R.mipmap.ic_launcher, pendingIntent, true
        );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(this.notificationChannel == null) {
                this.notificationChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_DESCRIPTION, NotificationManager.IMPORTANCE_DEFAULT);
                this.notificationChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                if(this.notificationManager == null) {
                    this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                }
                notificationManager.createNotificationChannel(this.notificationChannel);
            }
        }

        this.startForeground(Constants.NOTIFICATION_ID, notification);
    }

    public class ASBinder extends Binder {
        WebSocketService getService() {
            return WebSocketService.this;
        }
    }
}
