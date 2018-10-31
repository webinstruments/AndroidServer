package com.example.lol.androidsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import Logging.LogManager;
import Logging.LogcatLogger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogManager.setLogger(new LogcatLogger());
        Server s = new Server(12345);
        s.start();
    }
}
