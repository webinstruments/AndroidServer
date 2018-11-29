package org.androidsocket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.graphics.drawable.IconCompat;
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
import java.util.List;
import java.util.Map;

public class CustomAdapter extends ArrayAdapter {
    private IAdapter adapter;
    private int resourceId;
    private int[] textViewIds;
    private ArrayList<Object> items;

    public CustomAdapter(IAdapter adapter, int layoutResourceId, int[] textViewIds, ArrayList items) {
        super(adapter.getContext(), layoutResourceId, items);
        this.adapter = adapter;
        this.resourceId = layoutResourceId;
        this.items = items;
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

        Object data = items.get(position);
        String[] rowData = this.adapter.getRowContent(data);

        for (int i = 0; i < this.textViewIds.length; ++i) {
            holder.textViews[i].setText(rowData[i]);
        }

        return row;
    }

    public void update(ArrayList items) {
        if (this.items.size() != items.size()) {
            super.clear();
            for (Object item : items) {
                this.add(item);
            }
        }
        this.items = items;
        this.notifyDataSetChanged();
    }

    @Override
    public void clear() {
        super.clear();
        this.items = new ArrayList<>();
    }

    public int getDataCount() {
        return this.items.size();
    }

    private static class ViewHolder {
        public ViewHolder(int capacity) {
            this.textViews = new TextView[capacity];
        }

        TextView[] textViews;
    }
}
