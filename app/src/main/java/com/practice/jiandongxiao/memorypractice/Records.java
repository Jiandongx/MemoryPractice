package com.practice.jiandongxiao.memorypractice;

import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Records extends AppCompatActivity {
    CustomDBHandler customDB = new CustomDBHandler(this);
    ListView recordList;
    AlertDialog.Builder alert;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recordList = findViewById(R.id.recordList);
        ArrayAdapter<String[]> adapter = new CustomAdapter2(this, customDB.RecordsToString());
        TextView text = findViewById(R.id.no_record);
        if (adapter.isEmpty()){
            text.setText("没有记录");
        }

        recordList.setAdapter(adapter);

        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                alert = new AlertDialog.Builder(new ContextThemeWrapper(Records.this, R.style.AlertDialogTheme));

                alert.setTitle("删除此纪录？");

                // To cast the object to string array
                final String record = ((String[]) parent.getItemAtPosition(position))[1];
                final String date = ((String[]) parent.getItemAtPosition(position))[0];
                alert.setMessage("日期：" + date);

                alert.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SQLiteDatabase db = customDB.getWritableDatabase();
                        try {
                            db.execSQL("DELETE FROM past_records WHERE _record = '" + record + "'");
                            Toast.makeText(getBaseContext(),
                                    "删除成功！",
                                    Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            Toast.makeText(getBaseContext(),
                                    "删除失败！",
                                    Toast.LENGTH_SHORT).show();
                        }

                        db.close();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });

                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                dialog = alert.show();

//                TextView message = dialog.findViewById(android.R.id.message);
//                message.setGravity(Gravity.CENTER);

//                TextView title = dialog.findViewById(
//                        getApplicationContext().
//                                getResources().
//                                getIdentifier("alertTitle", "id", getPackageName()));
//                title.setGravity(Gravity.CENTER);

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.number_link_help:
                alert = new AlertDialog.Builder(new ContextThemeWrapper(Records.this, R.style.AlertDialogTheme));

                alert.setTitle("帮助");
                alert.setMessage("你可保存以前的联想过的数字以便练习长时间记忆。");
                alert.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                dialog = alert.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
