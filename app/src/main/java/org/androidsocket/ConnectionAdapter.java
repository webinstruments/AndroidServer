package org.androidsocket;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.androidsocket.Models.ActiveConnection;
import org.androidsocket.Models.ConnectionData;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConnectionAdapter extends ArrayAdapter {
    private Context context;
    private int resourceId;
    private ArrayList<ActiveConnection> connections;

    public ConnectionAdapter(Context context, int layoutResourceId, ArrayList<ActiveConnection> connections) {
        super(context, layoutResourceId, connections);
        this.context = context;
        this.resourceId = layoutResourceId;
        this.connections = connections;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
            row = inflater.inflate(this.resourceId, parent, false);

            holder = new ViewHolder();
            holder.tvAddress = (TextView) row.findViewById(R.id.tvDataAddress);
            holder.tvDelay = (TextView) row.findViewById(R.id.tvDataDelay);
            holder.tvPings = (TextView) row.findViewById(R.id.tvDataPings);
            holder.tvMisses = (TextView) row.findViewById(R.id.tvDataMisses);
            holder.tvDateTime = (TextView) row.findViewById(R.id.tvDataDateTime);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ActiveConnection data = connections.get(position);
        holder.tvAddress.setText(data.getRemoteAddress().replace("/", "") + ':' + data.getRemotePort());
        DecimalFormat df = new DecimalFormat("#.00");
        holder.tvDelay.setText(df.format(data.getAverageDelay()));
        holder.tvPings.setText(Long.toString(data.getPingCount()));
        holder.tvMisses.setText(Long.toString(data.getMisses()));
        holder.tvDateTime.setText(data.getDateTime("HH:mm:ss"));

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

    static class ViewHolder {
        TextView tvAddress;
        TextView tvDelay;
        TextView tvPings;
        TextView tvMisses;
        TextView tvDateTime;
    }
}
