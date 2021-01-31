package com.practice.jiandongxiao.memorypractice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    private EditText card_num;
    private EditText speed_num;
    private Switch hint_number_switch;
    private Switch hint_image_switch;
    private Button done_btn;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private NumberPicker range_num_1;
    private NumberPicker range_num_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        pref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = pref.edit();

        card_num = findViewById(R.id.card_num);
        card_num.setText(pref.getInt("Card", 2) + "");
        card_num.setSelection(card_num.getText().length());

        speed_num = findViewById(R.id.speed_num);
        speed_num.setText(pref.getInt("Speed", 2) + "");

        hint_number_switch = findViewById(R.id.hint_string_switch);
        hint_number_switch.setChecked(pref.getBoolean("Switch", true));

        hint_image_switch = findViewById(R.id.hint_image_switch);
        hint_image_switch.setChecked(pref.getBoolean("Switch2", true));

        range_num_1 = findViewById(R.id.range_num_1);
        range_num_1.setMaxValue(99);
        range_num_1.setMinValue(0);
        range_num_1.setValue(pref.getInt("Range1", 0));

        range_num_2 = findViewById(R.id.range_num_2);
        range_num_2.setMaxValue(99);
        range_num_2.setMinValue(0);
        range_num_2.setValue(pref.getInt("Range2", 99));

        done_btn = findViewById(R.id.done_btn);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputSetting()) {
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                if (validateInputSetting()){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //check if no view has focus:
                    View view = this.getCurrentFocus();
                    if(view != null){
                        //imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validateInputSetting(){
        if (range_num_1.getValue() > range_num_2.getValue()){
            Toast.makeText(getBaseContext(),"错误！请选择正确的数字范围！", Toast.LENGTH_SHORT).show();
            return false;
        }else if (card_num.getText().toString().equals("") || Integer.parseInt(card_num.getText().toString()) == 0){
            card_num.requestFocus();
            card_num.setSelection(card_num.getText().length());
            Toast.makeText(getBaseContext(),"错误！数字卡片数量要大于1！", Toast.LENGTH_SHORT).show();
            return false;
        }else if (speed_num.getText().toString().equals("") || Integer.parseInt(speed_num.getText().toString()) == 0) {
            speed_num.requestFocus();
            speed_num.setSelection(speed_num.getText().length());
            Toast.makeText(getBaseContext(),"错误！显示卡片的时间要大于0！", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            editor.putInt("Range1", range_num_1.getValue());
            editor.putInt("Range2", range_num_2.getValue());
            editor.putInt("Card", Integer.parseInt(card_num.getText().toString()));
            editor.putInt("Speed", Integer.parseInt(speed_num.getText().toString()));
            editor.putBoolean("Switch", hint_number_switch.isChecked());
            editor.putBoolean("Switch2", hint_image_switch.isChecked());
            editor.commit();
            setResult(RESULT_OK);
            return true;
        }
    }
}
