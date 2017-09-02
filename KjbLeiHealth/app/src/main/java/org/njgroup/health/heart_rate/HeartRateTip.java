package org.njgroup.health.heart_rate;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.njgroup.health.R;

/**
 * 心率用户提示界面
 *
 * @author LeiYu
 */

public class HeartRateTip {
    private TextView tv;// 文字
    private ImageView iv;// 图片

    //构造器:初始化组件
    public HeartRateTip(Activity activity) {
        tv = (TextView) activity.findViewById(R.id.tv_tip);
        iv = (ImageView) activity.findViewById(R.id.iv_tip);
    }

    //隐藏提示
    public void hide() {
        tv.setVisibility(View.GONE);
        iv.setVisibility(View.GONE);
    }

    //显示提示
    public void show(String text) {
        if (text == null) text = "";
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
        iv.setVisibility(View.VISIBLE);
    }
}
