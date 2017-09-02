package org.njgroup.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.njgroup.health.breath_speed.BreathSpeedActivity;
import org.njgroup.health.ear_listen.EarListenActivity;
import org.njgroup.health.heart_rate.HeartRateActivity;

/**
 * 主界面类
 *
 * @author WangLiMei
 */

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主界面布局
        setContentView(R.layout.activity_main);
    }

    //主界面上“心率检测”的点击事件
    public void onHeartRateClick(View view) {
        startActivity(new Intent(this, HeartRateActivity.class));//打开“心率检测”界面
    }

    //主界面上“听力测试”的点击事件
    public void onEarListenClick(View view) {
        startActivity(new Intent(this, EarListenActivity.class));  //打开“听力测试”界面
    }

    //主界面上“呼吸频率”的点击事件
    public void onBreathSpeedClick(View view) {
        startActivity(new Intent(this, BreathSpeedActivity.class));//打开“呼吸频率”界面
    }

}
