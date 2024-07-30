package com.example.missionstatement.GV;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<String[]> data;
    private LayoutInflater inflater;

    public GridAdapter(Context context, List<String[]> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size() * 2; // כל שורה מכילה 2 תאים
    }

    @Override
    public Object getItem(int position) {
        int rowIndex = position / 2;
        int columnIndex = position % 2;
        return data.get(rowIndex)[columnIndex];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText((String) getItem(position));

        return convertView;
    }
}

