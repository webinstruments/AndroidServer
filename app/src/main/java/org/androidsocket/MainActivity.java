package org.androidsocket;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.logging.LogManager;
import org.logging.LogcatLogger;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PollSlider ps = new PollSlider((SeekBar) this.findViewById(R.id.sliderPoll),
                (TextView) this.findViewById(R.id.tvPoll), 10,
                this.sliderValues, 0, this);

        StartButton b = new StartButton((Button) this.findViewById(R.id.btnStartServer), this);

        LogManager.setLogger(new LogcatLogger());
        this.server = new Server(12345, ps.getValue());
        this.server.setReuseAddr(true);
    }

    public void startServer() {
        this.server.start();
    }

    public void stopServer() {
        this.server.stop();
    }

    public void setPollingInterval(int value) {
        this.server.setTimeout(value);
    }

    public Boolean isServerRunning() {
        return this.server.isRunning();
    }

    Activity activity;
    final int[] sliderValues = {
            50,
            100,
            500,
            1000,
            2000,
            5000,
            10000,
            20000,
            30000,
            45000,
            60000
    };

    class PollSlider implements SeekBar.OnSeekBarChangeListener {

        public PollSlider(SeekBar slider, TextView output, int max, int[] sliderValues, int defaultStep, MainActivity parent) {
            this.slider = slider;
            this.output = output;
            this.parent = parent;
            this.values = sliderValues;
            this.slider.setMax(max);
            this.slider.setProgress(defaultStep);
            this.displayText(this.values[defaultStep]);

            this.slider.setOnSeekBarChangeListener(this);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            this.displayText(this.values[i]);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            this.parent.setPollingInterval(this.getValue());
        }

        public int getValue() {
            return this.values[this.slider.getProgress()];
        }

        private void displayText(int progress) {
            this.output.setText(Integer.toString(progress));
        }

        SeekBar slider;
        TextView output;
        int[] values;
        MainActivity parent;
    }

    class StartButton implements View.OnClickListener {

        public StartButton(Button b, MainActivity parent) {
            this.button = b;
            this.parent = parent;
            this.button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            parent.startServer();
            this.button.setText(R.string.btn_started);
            this.button.setEnabled(false);
        }

        Button button;
        MainActivity parent;
    }

    Server server;
}
