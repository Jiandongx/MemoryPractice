package com.practice.jiandongxiao.memorypractice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NumberLinkageList extends AppCompatActivity {
    private ListView numberList;
    CustomDBHandler customDB = new CustomDBHandler(this);
    AlertDialog.Builder alert;
    AlertDialog dialog;
    String input;
    private ImageView imageView;
    private ImageSaver imageSaver = new ImageSaver(this);;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_linkage);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        numberList = findViewById(R.id.numberList);

        setupNumberList();
    }

    public void setupNumberList() {

        adapter = new CustomAdapter(this, customDB.DatabaseToString());

        adapter.notifyDataSetChanged();

        numberList.setAdapter(adapter);

        numberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                Intent i = new Intent(NumberLinkageList.this, NumberLinkage.class);
                i.putExtra("number", position);
                startActivityForResult(i, 1);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Request Code", String.valueOf(requestCode));
        Log.d("Result Code", String.valueOf(resultCode));
        switch(requestCode) {
            case (1) : {
                if (resultCode == RESULT_OK) {
                    Log.d("Check Intent back", String.valueOf(data.getIntExtra("number", -1)));
                    setupNumberList();
                    numberList.setSelectionFromTop(data.getIntExtra("number", 0), 0);
                }
                break;
            }
        }
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
                alert = new AlertDialog.Builder(new ContextThemeWrapper(NumberLinkageList.this, R.style.AlertDialogTheme));

                alert.setTitle("帮助");
                alert.setMessage("0-11及22以形状相似的东西为引子，其余的皆以谐音为引子，方便记忆。\n" +
                        "你可点击更改联想列表。");
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
