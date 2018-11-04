package org.androidsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.logging.LogManager;
import org.logging.LogcatLogger;

public class MainActivity extends AppCompatActivity {
    Server s = new Server(12345);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogManager.setLogger(new LogcatLogger());
        s.setReuseAddr(true);
        s.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogManager.getLogger().info("Stopping Server");
        try {
            s.stop(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
