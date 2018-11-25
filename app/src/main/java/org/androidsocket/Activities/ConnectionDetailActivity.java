package org.androidsocket.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidsocket.Constants;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.R;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class ConnectionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        Bundle extras = getIntent().getExtras();
        this.connectionIndex = extras.getInt(Constants.CONNECTION_DETAIL_INTENT_KEY);
        ActiveConnection c = ConnectionData.getConnectionFromIndex(this.connectionIndex);
        this.latencyListView = (ListView)findViewById(R.id.lvLatencies);
        this.latencies = new ArrayAdapter<Long>(this, R.layout.latency_item, R.id.tvLatencyItem, c.getLatencies());
        this.latencyListView.setAdapter(this.latencies);
        this.addressText = (TextView)findViewById(R.id.tvDetailAddress);
        this.dateTimeText = (TextView)findViewById(R.id.tvDetailDateTime);
        this.pingsText = (TextView)findViewById(R.id.tvDetailPings);
        this.missesText = (TextView)findViewById(R.id.tvDetailMisses);
        this.statisticsText = (TextView)findViewById(R.id.tvDetailStatistics);
        this.minText = (TextView)findViewById(R.id.tvDetailMin);
        this.maxText = (TextView)findViewById(R.id.tvDetailMax);
        this.updateForm(c);
    }

    private void updateForm(ActiveConnection c) {
        this.addressText.setText(c.getRemoteAddress().replace("/", "") + ":" + c.getRemotePort());
        this.dateTimeText.setText(c.getDateTime("dd.MM.yyyy HH:mm:ss"));
        this.pingsText.setText(Long.toString(c.getPingCount()));
        this.missesText.setText(Long.toString(c.getMisses()));
        double percentage = c.getPingCount() != 0 ? (c.getPingCount() - c.getMisses()) / (double)c.getPingCount() : 0;
        DecimalFormat df = new DecimalFormat("#.00");
        this.statisticsText.setText(df.format(percentage));
        this.minText.setText(Long.toString(c.getMinLatency()) + " ms");
        this.maxText.setText(Long.toString(c.getMaxLetency()) + " ms");
    }

    private int connectionIndex;
    private ListView latencyListView;
    private ArrayAdapter<Long> latencies;
    private TextView addressText;
    private TextView dateTimeText;
    private TextView pingsText;
    private TextView missesText;
    private TextView statisticsText;
    private TextView minText;
    private TextView maxText;
}
