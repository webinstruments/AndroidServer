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
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.R;
import org.logging.LogManager;

public class ConnectionsActivity extends AppCompatActivity implements Observer {

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectionData.removeObserver(this);
        if (this.timer != null) {
            this.timer.interrupt();
            this.timer = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionData.addObserver(this);
        startTimer(3000);
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

    private void startTimer(final long timeout) {
        if (timer == null) {
            this.timer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        LogManager.getLogger().info("Hello");
                        ConnectionsActivity.this.update = true;
                        ConnectionsActivity.this.update();
                        try {
                            Thread.sleep(timeout);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            });
            this.timer.start();
        }
    }

    private ConnectionAdapter adapter;
    private ListView dataList;
    private boolean update;
    private Thread timer;
}
