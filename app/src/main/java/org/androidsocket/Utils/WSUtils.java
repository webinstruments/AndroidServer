package org.androidsocket.Utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import org.androidsocket.Utils.Constants;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.Models.Latency;
import org.java_websocket.WebSocket;
import org.logging.LogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    public static File createFile(Context context, ArrayList<ActiveConnection> connections) throws IOException {
        String filename = generateFileName();
        String directory = context.getFilesDir() + "/lists/";
        File outputDirectory = new File(directory);
        if(outputDirectory.exists()) {
            deleteFilesFromFolder(outputDirectory);
        }
        outputDirectory.mkdirs();
        File file = new File(outputDirectory, filename);
        FileOutputStream fStream = new FileOutputStream(file, true);

        String delimiter = ";";
        for(int i = 0; i < connections.size(); ++i) {
            writeLine(fStream, delimiter, "RemoteAddress", "DateTime");
            ActiveConnection conn = connections.get(i);
            writeLine(fStream, delimiter, conn.getFullRemoteAddress(), conn.getDateTime("dd.MM.yyyy HH:mm:ss"));
            ArrayList<Latency> latencies = connections.get(i).getLatencies();
            for(int j = 0; j < latencies.size(); ++j) {
                Latency latencyData = latencies.get(j);
                writeLine(fStream, delimiter,
                        latencyData.getLatency(), latencyData.getServiceName(), latencyData.getSignalStrength());
            }
        }

        fStream.close();

        return file;
    }

    private static String generateFileName() {
        SimpleDateFormat df = new SimpleDateFormat("ddMMyy_HHmmss");
        return "file_" + df.format(new Date(System.currentTimeMillis())) + ".csv";
    }

    private static void deleteFilesFromFolder(File directory) {
        File[] files = directory.listFiles();
        if(files != null) {
            for (File f: files) {
                f.delete();
            }
        }
    }

    private static void writeLine(FileOutputStream stream, String delimitter, Object ...args) throws IOException {
        String line = "";
        for(int i = 0; i < args.length; ++i) {
            if(i < args.length - 1)
                line += args[i] + delimitter;
            else
                line += args[i] + "\r\n";
        }
        stream.write(line.getBytes());
    }
}
