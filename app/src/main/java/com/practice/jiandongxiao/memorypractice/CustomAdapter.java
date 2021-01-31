package com.practice.jiandongxiao.memorypractice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by jiandongxiao on 4/7/15.
 */
public class CustomAdapter extends ArrayAdapter<String> {
    public CustomAdapter(NumberLinkageList context, String[] items) {
        super(context,R.layout.custom_row ,items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater customInflator = LayoutInflater.from(getContext());
        View customView = customInflator.inflate(R.layout.custom_row, parent, false);
        String item = getItem(position);
        TextView text = customView.findViewById(R.id.singleItem);
        text.setText((position) + " - " + item);

        return customView;
    }
}
