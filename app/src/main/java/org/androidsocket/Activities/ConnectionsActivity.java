package org.androidsocket.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.androidsocket.ConnectionAdapter;
import org.androidsocket.Constants;
import org.androidsocket.Interfaces.Observer;
import org.androidsocket.Interfaces.TimerObserver;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.R;
import org.androidsocket.Utils.UpdateTimer;
import org.logging.LogManager;

public class ConnectionsActivity extends AppCompatActivity implements Observer, TimerObserver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        this.dataList = (ListView) findViewById(R.id.lvConnections);
        this.adapter = new ConnectionAdapter(this, R.layout.connection_data, ConnectionData.getActiveConnections());
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

    private ConnectionAdapter adapter;
    private ListView dataList;
    private boolean update;
    private UpdateTimer timer;
}
