package com.example.lol.androidsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

import Logging.LogManager;
import Logging.LogcatLogger;

public class MainActivity extends AppCompatActivity {
    Server s = new Server(12345);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogManager.setLogger(new LogcatLogger());
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
