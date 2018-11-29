package org.androidsocket.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidsocket.Adapter.CustomAdapter;
import org.androidsocket.Constants;
import org.androidsocket.Interfaces.IAdapter;
import org.androidsocket.Interfaces.Observer;
import org.androidsocket.Interfaces.TimerObserver;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.Models.Latency;
import org.androidsocket.R;
import org.androidsocket.Utils.UpdateTimer;
import org.logging.LogManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDetailActivity extends AppCompatActivity implements Observer, TimerObserver, IAdapter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        Bundle extras = getIntent().getExtras();
        this.connectionIndex = extras.getInt(Constants.CONNECTION_DETAIL_INTENT_KEY);
        ActiveConnection c = ConnectionData.getConnectionFromIndex(this.connectionIndex);
        this.latencyListView = (ListView) findViewById(R.id.lvLatencies);
        //this.latencies = new ArrayAdapter<Long>(this, R.layout.latency_item, R.id.tvLatencyItem, c.getLatencies());
        this.latencies = new CustomAdapter(this, R.layout.latency_item, new int[] {
                R.id.tvDetailListLatency,
                R.id.tvDetailListType,
                R.id.tvDetailListStrength,
        }, c.getLatencies());
        this.latencyListView.setAdapter(this.latencies);
        this.addressText = (TextView) findViewById(R.id.tvDetailAddress);
        this.dateTimeText = (TextView) findViewById(R.id.tvDetailDateTime);
        this.pingsText = (TextView) findViewById(R.id.tvDetailPings);
        this.missesText = (TextView) findViewById(R.id.tvDetailMisses);
        this.statisticsText = (TextView) findViewById(R.id.tvDetailStatistics);
        this.averageText = (TextView) findViewById(R.id.tvDetailAverage);
        this.minText = (TextView) findViewById(R.id.tvDetailMin);
        this.maxText = (TextView) findViewById(R.id.tvDetailMax);
        this.updateForm(c);
        this.timer = new UpdateTimer(this, 3000);
        this.update = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectionData.removeObserver(this);
        this.timer.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionData.addObserver(this);
        this.timer.start();
    }

    private void updateForm(ActiveConnection c) {
        if(c == null) {
            LogManager.getLogger().warning("Connection erased");
            finish();
        }
        this.addressText.setText(c.getRemoteAddress().replace("/", "") + ":" + c.getRemotePort());
        this.dateTimeText.setText(c.getDateTime("dd.MM.yyyy HH:mm:ss"));
        this.pingsText.setText(Long.toString(c.getPingCount()));
        this.missesText.setText(Long.toString(c.getMisses()));
        double percentage = c.getPingCount() != 0 ? (c.getPingCount() - c.getMisses()) / (double) c.getPingCount() * 100 : 0;
        DecimalFormat df = new DecimalFormat("#.00");
        this.statisticsText.setText(df.format(percentage));
        this.averageText.setText(df.format(c.getAverageDelay()) + " ms");
        this.minText.setText(Long.toString(c.getMinLatency()) + " ms");
        this.maxText.setText(Long.toString(c.getMaxLetency()) + " ms");
    }

    @Override
    public void update() {
        if(this.update) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActiveConnection c = ConnectionData.getConnectionFromIndex(ConnectionDetailActivity.this.connectionIndex);
                    ConnectionDetailActivity.this.updateForm(c);
                    ArrayList<Latency> latencies = c.getLatencies();
                    if(latencies.size() != ConnectionDetailActivity.this.latencies.getCount()) {
                        ConnectionDetailActivity.this.latencies.clear();
                        List<Latency> latenciesToUpdate = new ArrayList<>();
                        for(int i = 0; i < latencies.size() && i < 50; ++i) {
                            latenciesToUpdate.add(latencies.get(i));
                        }
                        ConnectionDetailActivity.this.latencies.update(latencies);
                    }
                    ConnectionDetailActivity.this.update = false;
                }
            });
        }
    }

    @Override
    public void onTimerUpdate() {
        this.update = true;
        this.update();
    }

    @Override
    public String[] getRowContent(Object rowData) {
        Latency l = (Latency)rowData;
        return new String[] {
                Long.toString(l.latency),
                l.serviceName,
                l.strength
        };
    }

    @Override
    public Context getContext() {
        return this;
    }

    private int connectionIndex;
    private ListView latencyListView;
    private CustomAdapter latencies;
    private TextView addressText;
    private TextView dateTimeText;
    private TextView pingsText;
    private TextView missesText;
    private TextView statisticsText;
    private TextView averageText;
    private TextView minText;
    private TextView maxText;
    private UpdateTimer timer;
    private boolean update;
}
