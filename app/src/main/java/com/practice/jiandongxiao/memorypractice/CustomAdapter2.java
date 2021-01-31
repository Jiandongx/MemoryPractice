package com.practice.jiandongxiao.memorypractice;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by jiandongxiao on 24/9/15.
 */
public class CustomAdapter2 extends ArrayAdapter<String[]> {

    public CustomAdapter2(Context context, String[][] items) {
        super(context, R.layout.custom_row, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater customInflator = LayoutInflater.from(getContext());
        View customView = customInflator.inflate(R.layout.custom_row, parent, false);

        String[] item = getItem(position);
        TextView text = customView.findViewById(R.id.singleItem);

        // Get the DAY of the date and convert to Chinese
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dayInChinese = item[0];
        try {
            String dayOfTheWeek = (String) DateFormat.format("EEE", formatter.parse(item[0])); // Thursday
            switch (dayOfTheWeek) {
                case "Sun":
                    dayInChinese = dayInChinese + " 星期天";
                    break;
                case "Sat":
                    dayInChinese = dayInChinese + " 星期六";
                    break;
                case "Mon":
                    dayInChinese = dayInChinese + " 星期一";
                    break;
                case "Tue":
                    dayInChinese = dayInChinese + " 星期二";
                    break;
                case "Wed":
                    dayInChinese = dayInChinese + " 星期三";
                    break;
                case "Thu":
                    dayInChinese = dayInChinese + " 星期四";
                    break;
                case "Fri":
                    dayInChinese = dayInChinese + " 星期五";
                    break;
            }
        } catch (Exception e) {
            Log.e("SimpleDateFormat", "Error converting string to date");
        }

        StringBuilder result = new StringBuilder();

        // Append the date field first
        result.append(dayInChinese + '\n');

        // Create 1 space for every 2 number for the History Record
        for (int i = 0; i < item[1].length(); i++) {
            if (i % 2 == 0 && i != 0) {
                result.append(" ");
            }

            result.append(item[1].charAt(i));
        }
        text.setText(result.toString());

        return customView;
    }
}
