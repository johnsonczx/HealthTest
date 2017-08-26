package org.njgroup.health;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.njgroup.health.wanglimei.breathe.BreatheActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener{
    /** Called when the activity is first created. */
    private Button heartBtn = null;
    private Button hearingBtn = null;
    private Button breatheBtn = null;
    private TextView textView;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
        heartBtn = (Button) this.findViewById(R.id.heartbtn);
        hearingBtn = (Button) this.findViewById(R.id.hearingbtn);
        breatheBtn = (Button) this.findViewById(R.id.breathebtn);

//        TextView view = (TextView) findViewById(android.R.id.title);
//        view.setGravity(Gravity.CENTER);
        heartBtn.setOnClickListener(this);
        hearingBtn.setOnClickListener(this);
        breatheBtn.setOnClickListener(this);


    }

    public void onClick(View v) {

        //测心率
        if(v==heartBtn)
        {
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, HeartRateActivity.class);
//            MainActivity.this.startActivity(intent);

        }
        if(v==hearingBtn)
        {

        }
        //测听力
        //测呼吸频率
        if (v == breatheBtn) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, BreatheActivity.class);
            MainActivity.this.startActivity(intent);

        }
    }
}
