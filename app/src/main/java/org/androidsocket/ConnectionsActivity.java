package org.androidsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ConnectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        TableRow row = new TableRow(this);
        TextView text = new TextView(this);
        TableLayout header = (TableLayout) this.findViewById(R.id.tableHeader);
        text.setText("ROW 1");
        row.addView(row);
        header.addView(row);
    }
}
