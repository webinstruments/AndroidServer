package org.androidsocket.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.androidsocket.ConnectionAdapter;
import org.androidsocket.Interfaces.Observer;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.R;

public class ConnectionsActivity extends AppCompatActivity implements Observer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        this.dataList = (ListView)findViewById(R.id.lvConnections);
        this.adapter = new ConnectionAdapter(this, R.layout.connection_data, ConnectionData.getActiveConnections());
        this.dataList.setAdapter(adapter);
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

    @Override
    public void update() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.update(ConnectionData.getActiveConnections());
            }
        });
    }


    private ConnectionAdapter adapter;
    private ListView dataList;
}
