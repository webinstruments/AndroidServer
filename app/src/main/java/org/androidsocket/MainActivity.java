package org.androidsocket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.logging.LogManager;
import org.logging.LogcatLogger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.pollSlider = new PollSlider((SeekBar) this.findViewById(R.id.sliderPoll),
                (TextView) this.findViewById(R.id.tvPoll), 10,
                this.sliderValues, 0);

        this.pollingBtn = (Button) findViewById(R.id.btnPolling);
        this.pollingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                if (MainActivity.this.isServerRunning()) {
                    MainActivity.this.stopServer();
                    btn.setText(R.string.btn_state_stopped);
                } else {
                    MainActivity.this.startServer();
                    btn.setText(R.string.btn_state_started);
                }
            }
        });

        this.serviceBtn = (Button) findViewById(R.id.btnService);
        this.serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                if(MainActivity.this.bound) {
                    MainActivity.this.stopService();
                    btn.setText(R.string.btn_service_stopped);
                } else {
                    MainActivity.this.startService();
                    btn.setText(R.string.btn_service_started);
                }
            }
        });

        LogManager.setLogger(new LogcatLogger());

        this.serviceIntent = new Intent(this, WebSocketService.class);
        this.bindService(this.serviceIntent, this.connection, Context.BIND_AUTO_CREATE);
        this.stateView = (TextView) this.findViewById(R.id.ServerState);
        this.synchronize();
    }

    @Override
    protected void onStop() {
        LogManager.getLogger().info("OnStop");
        super.onStop();
    }

    public void startServer() {
        if (!this.service.isStarted()) {
            startService(this.serviceIntent);
        }
        this.service.start(12345, this.pollSlider.getValue());
        this.synchronize();
    }

    public void stopServer() {
        this.service.stop();
        this.synchronize();
    }

    public void startService() {
        this.startService(this.serviceIntent);
        this.bound = true;
        synchronize();
    }

    public void stopService() {
        this.bound = false;
        this.synchronize();
        this.service.stopService();
    }

    public void setPollingInterval(int value) {
        this.service.setTimeout(value);
    }

    public Boolean isServerRunning() {
        return this.service.isStarted();
    }

    public void synchronize() {
        this.pollingBtn.setEnabled(true);
        this.serviceBtn.setText(R.string.btn_service_started);
        if (!this.bound) {
            this.stateView.setText(R.string.state_connecting);
            this.pollingBtn.setText(R.string.btn_state_stopped);
            if (this.pollingBtn.isEnabled()) {
                this.pollingBtn.setEnabled(false);
            }
            this.serviceBtn.setText(R.string.btn_service_stopped);
        } else if (this.service.isStarted()) {
            this.stateView.setText(R.string.state_started);
            this.pollSlider.setValue(this.service.getTimeout());
            this.pollingBtn.setText(R.string.btn_state_started);
        } else {
            this.stateView.setText(R.string.state_stopped);
            this.pollingBtn.setText(R.string.btn_state_stopped);
        }
    }

    final int[] sliderValues = {
            50, 100, 500, 1000, 2000, 5000,
            10000, 20000, 30000, 45000, 60000
    };

    class PollSlider implements SeekBar.OnSeekBarChangeListener {

        public PollSlider(SeekBar slider, TextView output, int max, int[] sliderValues, int defaultStep) {
            this.slider = slider;
            this.output = output;
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
            MainActivity.this.setPollingInterval(this.getValue());
        }

        public int getValue() {
            return this.values[this.slider.getProgress()];
        }

        public void setValue(int value) {
            for (int i = 0; i < values.length; ++i)
                if (values[i] == value) {
                    this.slider.setProgress(i);
                    break;
                }
        }

        private void displayText(int progress) {
            this.output.setText(Integer.toString(progress));
        }

        SeekBar slider;
        TextView output;
        int[] values;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WebSocketService.ASBinder binder = (WebSocketService.ASBinder) iBinder;
            MainActivity.this.service = ((WebSocketService.ASBinder) iBinder).getService();
            LogManager.getLogger().info("Connected with Service [%s]", MainActivity.this.service.getID());
            MainActivity.this.bound = true;
            MainActivity.this.synchronize();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogManager.getLogger().warning("OnServiceDisconnected");
            MainActivity.this.bound = false;
            MainActivity.this.synchronize();
        }
    };

    WebSocketService service;
    TextView stateView;
    Button pollingBtn;
    Button serviceBtn;
    Intent serviceIntent;
    boolean bound = false;
    PollSlider pollSlider;
}
