package org.androidsocket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.androidsocket.Interfaces.IAdapter;
import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;
import org.androidsocket.R;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class CustomAdapter extends ArrayAdapter {
    private IAdapter adapter;
    private int resourceId;
    private int[] textViewIds;
    private ArrayList<ActiveConnection> connections;

    public CustomAdapter(IAdapter adapter, int layoutResourceId, int[] textViewIds, ArrayList<ActiveConnection> connections) {
        super(adapter.getContext(), layoutResourceId, connections);
        this.adapter = adapter;
        this.resourceId = layoutResourceId;
        this.connections = connections;
        this.textViewIds = textViewIds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) this.adapter.getContext()).getLayoutInflater();
            row = inflater.inflate(this.resourceId, parent, false);

            holder = new ViewHolder(this.textViewIds.length);
            for (int i = 0; i < this.textViewIds.length; ++i) {
                holder.textViews[i] = (TextView) row.findViewById(this.textViewIds[i]);
            }

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ActiveConnection data = connections.get(position);
        String[] rowData = this.adapter.getRowContent(data);

        for(int i = 0; i < this.textViewIds.length; ++i) {
            holder.textViews[i].setText(rowData[i]);
        }

        return row;
    }

    public void update(ArrayList<ActiveConnection> connections) {
        if (this.connections.size() != connections.size()) {
            this.clear();
            for (ActiveConnection conn : connections) {
                this.add(conn);
            }
        }
        this.connections = connections;
        this.notifyDataSetChanged();
    }

    public int getDataCount() {
        return this.connections.size();
    }

    private static class ViewHolder {
        public ViewHolder(int capacity) {
            this.textViews = new TextView[capacity];
        }

        TextView[] textViews;
    }
}
