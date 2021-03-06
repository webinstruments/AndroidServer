package org.androidsocket.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.androidsocket.Interfaces.Observer;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.R;
import org.androidsocket.WebSocketService;
import org.logging.LogManager;
import org.logging.LogcatLogger;

public class MainActivity extends AppCompatActivity implements Observer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectionData.initSignal(this);

        this.pollSlider = new PollSlider((SeekBar) this.findViewById(R.id.sliderPoll),
                (TextView) this.findViewById(R.id.tvPoll), 10,
                this.sliderValues, 0);

        this.serverBtn = (Button) findViewById(R.id.btnServer);
        this.serverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                if(MainActivity.this.service.isServerStarted()) {
                    MainActivity.this.stopServer();
                    btn.setText(R.string.btn_server_stopped);
                } else {
                    MainActivity.this.startServer();
                    btn.setText(R.string.btn_server_started);
                }
            }
        });

        this.pollingBtn = (Button) findViewById(R.id.btnPolling);
        this.pollingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                if (MainActivity.this.service.isPollingStarted()) {
                    MainActivity.this.stopPolling();
                    btn.setText(R.string.btn_state_stopped);
                } else {
                    MainActivity.this.startPolling();
                    btn.setText(R.string.btn_state_started);
                }
            }
        });

        this.serviceBtn = (Button) findViewById(R.id.btnService);
        this.serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                if (MainActivity.this.bound) {
                    MainActivity.this.stopService();
                    btn.setText(R.string.btn_service_stopped);
                } else {
                    MainActivity.this.startService();
                    btn.setText(R.string.btn_service_started);
                }
            }
        });

        LogManager.setLogger(new LogcatLogger());

        this.statisticsBtn = (Button) findViewById(R.id.btnConnections);
        this.statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionData.count();
                Intent i = new Intent(MainActivity.this, ConnectionsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        this.serviceIntent = new Intent(this, WebSocketService.class);
        this.bindService(this.serviceIntent, this.connection, Context.BIND_AUTO_CREATE);
        this.stateView = (TextView) this.findViewById(R.id.tvServerState);
        this.connectionView = (TextView) this.findViewById(R.id.tvConnections);
        this.synchronize();
        this.connections = -1;
        this.update();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectionData.removeObserver(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionData.addObserver(this);
    }

    public void startServer() {
        if (!this.service.isServerStarted()) {
            this.startService();
        }
        this.service.startServer(12345, this.pollSlider.getValue());
        this.synchronize();
    }

    public void stopServer() {
        this.service.stopServer();
        this.synchronize();
    }

    public void startPolling() {
        this.service.startPolling(12345, this.pollSlider.getValue());
        synchronize();
    }

    public void stopPolling() {
        this.service.stopPolling();
        synchronize();
    }

    public void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(this.serviceIntent);
        } else {
            this.startService(this.serviceIntent);
        }
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

    public void synchronize() {
        this.pollingBtn.setEnabled(true);
        this.serverBtn.setEnabled(true);
        this.serviceBtn.setText(R.string.btn_service_started);
        if (!this.bound) {
            this.stateView.setText(R.string.state_connecting);
            this.pollingBtn.setText(R.string.btn_state_stopped);
            this.serverBtn.setText(R.string.btn_server_stopped);
            if (this.pollingBtn.isEnabled()) {
                this.pollingBtn.setEnabled(false);
            }
            if (this.serviceBtn.isEnabled()) {
                this.serverBtn.setEnabled(false);
            }
            this.serviceBtn.setText(R.string.btn_service_stopped);
        } else {
            if (this.service.isServiceStarted()) {
                this.serverBtn.setText(R.string.btn_server_started);
            } else {
                this.serviceBtn.setText(R.string.btn_service_stopped);
            }
            if(this.service.isServerStarted()) {
                this.stateView.setText(R.string.server_started);
                this.serverBtn.setText(R.string.btn_server_started);
                this.pollSlider.setValue(this.service.getTimeout());
            } else {
                this.serverBtn.setText(R.string.btn_server_stopped);
                this.stateView.setText(R.string.server_stopped);
            }
            if(this.service.isPollingStarted()) {
                this.stateView.setText(R.string.state_started);
                this.pollingBtn.setText(R.string.btn_state_started);
            } else {
                this.pollingBtn.setText(R.string.btn_state_stopped);
            }
        }
    }

    final int[] sliderValues = {
            50, 100, 500, 1000, 2000, 5000,
            10000, 20000, 30000, 45000, 60000
    };

    @Override
    public void update() {
        if(this.connections != ConnectionData.count()) {
            this.connections = ConnectionData.count();
            final String message = ConnectionData.count() > 0 ?
                    getResources().getString(R.string.active_connections) + " " + ConnectionData.count() : getResources().getString(R.string.no_connections);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectionView.setText(message);
                }
            });
        }
    }

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

    private WebSocketService service;
    private TextView stateView;
    private TextView connectionView;
    private Button pollingBtn;
    private Button serviceBtn;
    private Button serverBtn;
    private Button statisticsBtn;
    private Intent serviceIntent;
    private boolean bound = false;
    private PollSlider pollSlider;
    private int connections;
}
