package com.practice.jiandongxiao.memorypractice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.os.Handler;
import android.widget.Toast;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private Button confirm_btn;
    private Button start_btn;
    private TextView num;
    private EditText num_input;
    private int progressCounter;
    private TextView hint;
    private AlertDialog.Builder alert;
    private AlertDialog dialog;
    private TextView progressText;
    public static String[] sample_cn = {
            "呼啦圈", "树木", "鸭子", "耳朵", "旗帜", "钩子", "手枪", "手杖", "葫芦", "球拍",
            "棒球", "筷子", "婴儿", "医生", "浴室", "鹦鹉", "鱼柳", "鱼鳍", "篱笆", "泥鳅",
            "恶灵", "鳄鱼", "鸳鸯", "和尚", "盒子", "二胡", "河流", "耳机", "荷花", "喝酒",
            "森林", "鲨鱼", "仙鹤", "咸蛋", "绅士", "珊瑚", "三鹿", "山鸡", "桑巴舞", "香蕉",
            "司令", "睡衣", "四合院", "雪山", "石狮", "水母", "石榴", "司机", "雪花", "死囚",
            "屋顶", "狐狸", "木耳", "牡丹", "护士", "木屋", "蜗牛", "母鸡", "苦瓜", "五角",
            "榴莲", "轮椅", "驴儿", "留声机", "律师", "锣鼓", "溜溜", "油漆", "喇叭", "鹿角",
            "麒麟", "蜥蜴", "企鹅", "鸡蛋", "骑士", "积木", "犀牛", "七喜", "琵琶", "气球",
            "百灵鸟", "蚂蚁", "百合", "花生", "巴士", "芭蕾", "白柳", "白棋", "白板", "芭蕉扇",
            "精灵", "球衣", "球儿", "救生圈", "教师", "救护车", "酒楼", "酒器", "酒吧", "胶卷"
    };

    public static String[] sample_en = {
            "Holahoop", "Tree", "Duck", "Ear", "Flag", "Hook", "Gun", "Walking Stick", "Bottle Gourd", "Racket",
            "Baseball & Bat", "Chopstick", "Elves", "Bitcoin", "Valentines", "Spade", "鱼柳", "鱼鳍", "篱笆", "泥鳅",
            "恶灵", "鳄鱼", "鸳鸯", "和尚", "盒子", "二胡", "河流", "耳机", "荷花", "喝酒",
            "森林", "鲨鱼", "仙鹤", "咸蛋", "绅士", "珊瑚", "三鹿", "山鸡", "桑巴舞", "香蕉",
            "司令", "睡衣", "四合院", "雪山", "石狮", "水母", "石榴", "司机", "雪花", "死囚",
            "屋顶", "狐狸", "木耳", "牡丹", "护士", "木屋", "蜗牛", "母鸡", "苦瓜", "五角",
            "榴莲", "轮椅", "驴儿", "留声机", "律师", "锣鼓", "溜溜", "油漆", "喇叭", "鹿角",
            "麒麟", "蜥蜴", "企鹅", "鸡蛋", "骑士", "积木", "犀牛", "七喜", "琵琶", "气球",
            "百灵鸟", "蚂蚁", "百合", "花生", "巴士", "芭蕾", "白柳", "白棋", "白板", "芭蕉扇",
            "精灵", "球衣", "球儿", "救生圈", "教师", "救护车", "酒楼", "酒器", "酒吧", "胶卷"
    };

    public static String[] items = new String[100];

    private TextView highest_record;
    private TextView current_record;
    private SharedPreferences pref;
    private SharedPreferences pref_first_time;
    private SharedPreferences.Editor editor;
    private Boolean show_number_hint;
    private Boolean show_image_hint;
    private ImageView imageView;
    private int speed;
    private int max, min;
    CustomDBHandler customDB = new CustomDBHandler(this);
    private ImageSaver imageSaver = new ImageSaver(this);
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        System.arraycopy(customDB.DatabaseToString(), 0, items, 0, 100);

        // Same sharedpreferences shared throughout the application
        pref = getSharedPreferences("Settings", MODE_PRIVATE);
        editor = pref.edit();

        progressCounter = pref.getInt("Card", 2);

        speed = pref.getInt("Speed", 2);

        show_number_hint = pref.getBoolean("Switch", true);
        show_image_hint = pref.getBoolean("Switch2", true);

        max = pref.getInt("Range2", 99);
        min = pref.getInt("Range1", 0);

        highest_record = findViewById(R.id.highest_record);
        highest_record.setText("最高纪录: " + (pref.getInt("record", progressCounter) * 2) + " 个数字");

        current_record = findViewById(R.id.current_record);
        current_record.setText("目前纪录: " + (progressCounter * 2) + " 个数字");

        confirm_btn = findViewById(R.id.btn_confirm);
        confirm_btn.setEnabled(false);

        start_btn = findViewById(R.id.btn_start);

        num_input = findViewById(R.id.inputNum);
        num_input.setEnabled(false);
        num_input.setClickable(false);

        Resources r = getResources();
        int pix = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200,
                r.getDisplayMetrics());

        num_input.setWidth(pix);

        num = findViewById(R.id.num1);
        hint = findViewById(R.id.hint1);
        imageView = findViewById(R.id.imageView1);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] array = createNumber(progressCounter);
                displayFirstNumber(array, 0);
            }
        });

        pref_first_time = PreferenceManager.getDefaultSharedPreferences(this);
        if (!pref_first_time.getBoolean("StoreImage", false)) {

            new MyAsyncTask().execute();

            editor.putInt("record", 2);
            editor.commit();

            SharedPreferences.Editor editor2 = pref_first_time.edit();
            editor2.putBoolean("StoreImage", true);
            editor2.commit();
        }
    }

    // The types specified here are the input data type, the progress type, and the result type
    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // Create the Dialog first
            // then inflate the Progress Bar into the dialog
            // Have to inflate because Progress Bar layout is not in the Main layout
            // if include the progress bar in the main layout, it will have error that need to
            // remove the View first before it can put the view inside the dialog
            alert = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogTheme));
            alert.setView(View.inflate(MainActivity.this, R.layout.progress_bar, null))
                    .setTitle("加载图片，请稍等...")
                    .setCancelable(false);

            dialog = alert.show();

            // Find the progress bar view in order to show status update
            progressBar = dialog.findViewById(R.id.progressBar);
            progressText = dialog.findViewById(R.id.progressText);
        }

        @Override
        protected String doInBackground(String... strings) {
            for (int i = 0; i < 100; i++){
                final int finalJ = i + 1;
                progressBar.setProgress(finalJ);

                // Due to error:
                // Only the original thread that created a view hierarchy can touch its views.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressText.setText(finalJ + "/100");
                    }
                });

                Bitmap bm = BitmapFactory.decodeResource(
                        getResources(),
                        getResources().getIdentifier("n_" + i, "drawable", getPackageName()));
                imageSaver.setFileName("n_" + i + ".png");
                imageSaver.save(bm);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // Hide the progress bar
           // progressBar.setVisibility(ProgressBar.INVISIBLE);
            dialog.cancel();
        }
    }

    public void beginGame() {
        num_input.setText("");
        num_input.setEnabled(false);
        num_input.setClickable(false);

        start_btn.setText("开始");
        start_btn.setEnabled(true);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_btn.setEnabled(false);
                num.setText("");
                String[] array = createNumber(progressCounter);
                displayFirstNumber(array, 0);
            }
        });
    }

    public void displayFirstNumber(String[] array, int counter) {
        start_btn.setEnabled(false);
        int itemNum = Integer.parseInt(array[0]);
        num.setText(array[0]);
        if (show_number_hint) {
            hint.setText(items[itemNum]);
        }

        if (show_image_hint) {
            imageSaver.setFileName("n_" + itemNum + ".png");
            imageView.setImageBitmap(imageSaver.load());
        }

        if (progressCounter == 1) {
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                num.setText("");
                hint.setText("");
                imageView.setImageResource(android.R.color.transparent);
            }
        }, speed * 1000 - 100);

        displayRemainingNumber(array, counter + 1);

    }

    public String[] createNumber(int counter) {
        Random random = new Random();
        String[] arr = new String[counter];
        for (int i = 0; i < counter; i++) {
            int temp = random.nextInt(max - min + 1) + min;
            String temp2 = Integer.toString(temp);
            if (temp < 10) {
                temp2 = String.format("%02d", temp);
            }
            arr[i] = temp2;
            Log.d("TAG", arr[i] + "");
        }
        return arr;
    }

    public void displayRemainingNumber(final String[] array, final int counter) {
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (counter >= array.length) {
                    num.setText("");
                    hint.setText("");
                    imageView.setImageResource(android.R.color.transparent);
                    getInputText(array);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            num.setText("");
                            hint.setText("");
                            imageView.setImageResource(android.R.color.transparent);
                        }
                    }, speed * 1000);

                    int itemNum = Integer.parseInt(array[counter]);
                    num.setText(array[counter] + "");
                    if (show_number_hint) {
                        hint.setText(items[itemNum]);
                    }
                    if (show_image_hint) {
                        imageSaver.setFileName("n_" + itemNum + ".png");
                        imageView.setImageBitmap(imageSaver.load());
                    }

                    displayRemainingNumber(array, counter + 1);
                }
            }
        }, speed * 1000 + 200);
    }

    public void getInputText(final String[] array) {
        start_btn.setEnabled(true);
        start_btn.setText("重复");
        start_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_input.setText("");
                num_input.setEnabled(false);
                num_input.setClickable(false);
                confirm_btn.setEnabled(false);
                displayFirstNumber(array, 0);
            }
        });

        num_input.setEnabled(true);
        num_input.setClickable(true);

        confirm_btn.setEnabled(true);
        confirm_btn.setText("确定");
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = num_input.getText().toString();
                String initialNum = Arrays.toString(array).replaceAll(", ", "")
                        .replaceAll("\\[", "")
                        .replaceAll("\\]", "");

                if (initialNum.equals(temp)) {
                    Toast.makeText(getBaseContext(), "正确!", Toast.LENGTH_SHORT).show();
                    progressCounter++;
                    current_record.setText("目前纪录: " + (progressCounter * 2) + " 个数字");
                    if ((progressCounter * 2) > (pref.getInt("record", 2) * 2)) {
                        editor.putInt("record", progressCounter);
                        editor.commit();
                        highest_record.setText("最高纪录: " + (progressCounter * 2) + " 个数字");
                    }
                } else {
                    Toast.makeText(getBaseContext(), "错误!请再接再厉!", Toast.LENGTH_SHORT).show();
                    String display = "正确答案: " + initialNum;
                    num.setText(display);
                    Log.d("ARRAY", display);
                }
                storeRecordOption(initialNum);
                beginGame();
            }
        });
    }

    public void storeRecordOption(final String record) {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String formattedDate = df.format(c);

        confirm_btn.setText("保存数据");
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put("_record", record);
                cv.put("_date", formattedDate);
                SQLiteDatabase db = customDB.getWritableDatabase();
                db.insert("past_records", null, cv);
                Toast.makeText(getBaseContext(), "保存成功", Toast.LENGTH_SHORT).show();
                db.close();
                confirm_btn.setEnabled(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK) {
                    show_number_hint = pref.getBoolean("Switch", true);
                    show_image_hint = pref.getBoolean("Switch2", true);
                    speed = pref.getInt("Speed", 2);
                    progressCounter = pref.getInt("Card", 2);
                    max = pref.getInt("Range2", 99);
                    min = pref.getInt("Range1", 0);
                    current_record.setText("目前纪录: " + (progressCounter * 2) + " 个数字");
                    start_btn.setText("重新开始");
                    start_btn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] array = createNumber(progressCounter);
                            displayFirstNumber(array, 0);
                        }
                    });
                }
                break;
            }
            default:
                Log.d("DEFAULT", "NO RESULT");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.arraycopy(customDB.DatabaseToString(), 0, items, 0, 100);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, Settings.class);
            startActivityForResult(i, 1);
            return false;
        }

        if (id == R.id.action_demo) {
            Intent i = new Intent(this, Demo.class);
            startActivity(i);
            return false;
        }

        if (id == R.id.action_number_link) {
            Intent i = new Intent(this, NumberLinkageList.class);
            startActivity(i);
            return false;
        }

        if (id == R.id.action_instruction) {
            Intent i = new Intent(this, Instruction.class);
            startActivity(i);
            return false;
        }

        if (id == R.id.action_records) {
            Intent i = new Intent(this, Records.class);
            startActivity(i);
            return false;
        }

        return false;
    }
}
