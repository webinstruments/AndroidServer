package org.androidsocket.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidsocket.Utils.Constants;
import org.androidsocket.Adapter.CustomAdapter;
import org.androidsocket.Interfaces.IAdapter;
import org.androidsocket.Interfaces.Observer;
import org.androidsocket.Interfaces.TimerObserver;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.R;
import org.androidsocket.Utils.UpdateTimer;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConnectionsActivity extends AppCompatActivity implements Observer, TimerObserver, IAdapter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        this.dataList = (ListView) findViewById(R.id.lvConnections);
        this.adapter = new CustomAdapter(this, R.layout.connection_data, new int[] {
                R.id.tvDataAddress,
                R.id.tvDataDelay,
                R.id.tvDataPings,
                R.id.tvDataMisses,
                R.id.tvDataDateTime,
        }, ConnectionData.getActiveConnections());
        this.dataList.setAdapter(adapter);
        this.update = true;
        this.dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ConnectionsActivity.this, ConnectionDetailActivity.class);
                intent.putExtra(Constants.CONNECTION_DETAIL_INTENT_KEY, i);
                ConnectionsActivity.this.startActivity(intent);
            }
        });
        this.timer = new UpdateTimer(this, 3000);
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

    @Override
    public void update() {
        if (ConnectionData.count() != this.adapter.getDataCount() || this.update) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConnectionsActivity.this.adapter.update(ConnectionData.getActiveConnections());
                }
            });
            this.update = false;
        }
    }

    @Override
    public void onTimerUpdate() {
        this.update = true;
        this.update();
    }

    @Override
    public String[] getRowContent(Object rowData) {
        ActiveConnection data = (ActiveConnection) rowData;
        DecimalFormat df = new DecimalFormat("#.00");
        return new String[] {
                data.getFullRemoteAddress(),
                df.format(data.getAverageDelay()),
                Long.toString(data.getPingCount()),
                Long.toString(data.getMissesCount()),
                data.getDateTime("HH:mm:ss"),
        };
    }

    @Override
    public Context getContext() {
        return this;
    }

    private CustomAdapter adapter;
    private ListView dataList;
    private boolean update;
    private UpdateTimer timer;
}
