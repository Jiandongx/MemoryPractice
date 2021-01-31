package com.practice.jiandongxiao.memorypractice;

import android.os.Handler;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Demo extends AppCompatActivity {
    private Button start_demo_btn;
    private String[] demo_array = {"14","15","92","65","35","89","79","32","38","46",
            "26","43","38","32","79","50","28","84","19","71"};
//            "69","39","93","75","10","58","20","97","49","44",
//            "59","23","07","81","64","06","28","62","08","99",
//            "86","28","03","48","25","34","21","17","06","79",
//            "82","14","80","86","51","32","82","30","66","47",
//            "09","38","44","60","95","50","58","22","31","72",
//            "53","59","40","81","28","48","11","17","45","02",
//            "84","10","27","01","93","85","21","10","55","59",
//            "64","46","22","94","89","54","93","03","81","96"};
    private String[] demo_story = {"山巅上有一间<font color='#EE0000'>浴室</font><br>",
        "一天飞来了<font color='#EE0000'>鹦鹉</font>要偷窥<br>",
        "洗澡的人拿了<font color='#EE0000'>球儿</font>丢它<br>",
        "又敲打<font color='#EE0000'>锣鼓</font>吓它<br>",
        "最后用浴缸里的<font color='#EE0000'>珊瑚</font><br>",
        "现场制造一把<font color='#EE0000'>芭蕉扇</font><br>",
        "把它像<font color='#EE0000'>气球</font>一样扇走<br>",
        "它飘啊飘的进化成一只<font color='#EE0000'>仙鹤</font><br>",
        "刚巧被一群跳着<font color='#EE0000'>桑巴舞</font>的人看到<br>",
        "那群人拿起<font color='#EE0000'>石榴</font>把它打下来<br>",
        "结果它掉进<font color='#EE0000'>河流</font><br>",
        "顺着河流漂进<font color='#EE0000'>雪山</font><br>",
        "跳<font color='#EE0000'>桑巴舞</font>的人紧追不舍<br>",
        "和<font color='#EE0000'>仙鹤</font>展开了追逐战<br>",
        "最后关头仙鹤抓住一只<font color='#EE0000'>气球</font><br>",
        "飘上了<font color='#EE0000'>屋顶</font><br>",
        "那群人脚下绽放<font color='#EE0000'>荷花</font>，步步生莲般飘了上去<br>",
        "仙鹤吓得赶紧跳上空中<font color='#EE0000'>巴士</font><br>",
        "由<font color='#EE0000'>泥鳅</font>驼着车子穿行<br>",
        "谁知却钻进了巨大<font color='#EE0000'>蜥蜴</font>的腹中<br>落了个凄惨的结局<br>"};
    private TextView demo_num, demo_hint, demo_statements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        demo_num = findViewById(R.id.demo_num);
        demo_hint = findViewById(R.id.demo_hint);
        demo_statements = findViewById(R.id.demo_statements);

        start_demo_btn = findViewById(R.id.start_demo);
        start_demo_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemNum = Integer.parseInt(demo_array[0]);
                demo_num.setText(demo_array[0] + "");
                demo_hint.setText(MainActivity.sample_cn[itemNum]);
                Spanned temp = Html.fromHtml(demo_story[0]);
                demo_statements.setText(temp);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        demo_num.setText("");
                        demo_hint.setText("");
                        demo_statements.setText("");
                    }
                }, 3400);
                StartDemo(demo_array, 1);
            }
        });
        
    }

    public void StartDemo(final String[] array, final int counter){
        start_demo_btn.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        demo_num.setText("");
                        demo_hint.setText("");
                        demo_statements.setText("");
                    }
                },3400);
                if (counter >= array.length) {
                    demo_num.setText("");
                    demo_hint.setText("");
                    EndDemo();
                } else {
                    int itemNum = Integer.parseInt(array[counter]);
                    demo_num.setText(array[counter] + "");
                    demo_hint.setText(MainActivity.sample_cn[itemNum]);
                    Spanned temp = Html.fromHtml(demo_story[counter]);
                    demo_statements.setText(temp);
                    StartDemo(array, counter + 1);
                }
            }
        }, 3500);
    }

    public void EndDemo(){
        start_demo_btn.setEnabled(true);
        demo_statements.setText("现在开始回想整个故事情节。通过联想把相对应的数字列出来。" +
                "你会发现40个数字如此轻松的就能记住。只要你的想象能力够好，记住上百个数字也不是问题。");
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                demo_statements.setText("现在开始回想整个故事情节。通过联想把相对应的数字列出来。" +
                        "你会发现40个数字如此轻松的就能记住。只要你的想象能力够好，轻松记住上百个数字也不是问题。");
            }
        }, 3400);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
