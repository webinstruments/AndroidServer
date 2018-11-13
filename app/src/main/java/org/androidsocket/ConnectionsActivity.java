package org.androidsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.androidsocket.Models.ConnectionData;

public class ConnectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        TableLayout header = (TableLayout) this.findViewById(R.id.tableHeader);
        appendData(header, new Object[][]{{
                getResources().getString(R.string.table_header_client),
                getResources().getString(R.string.table_header_average),
                getResources().getString(R.string.table_header_datetime),
        }}, true);

        TableLayout data = (TableLayout) this.findViewById(R.id.tableData);
        appendData(data, ConnectionData.getTableData(), false);
    }

    private TableRow tableRow;
    private void appendData(TableLayout table, Object[][] data, boolean header) {
        if (data.length == 0) return;

        float rowWidth = 1 / (float) data[0].length;
        for (int i = 0; i < data.length; ++i) {
            tableRow = new TableRow(this);
            for (int j = 0; j < data[i].length; ++j) {
                String colData = data[i][j].toString();
                tableRow.addView(createText(colData, rowWidth));
            }
            if(header) {
                table.addView(tableRow, 0);
            } else {
                table.addView(tableRow);
            }
        }
    }

    private TextView tableText;
    private TextView createText(String value, float width) {
        tableText = new TextView(this);
        tableText.setText(value);
        tableText.setLayoutParams(new TableRow.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                width
        ));
        return tableText;
    }
}
