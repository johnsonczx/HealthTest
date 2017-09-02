package org.njgroup.health;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Activity基类，用于综合事件处理
 *
 * @author LeiYu
 */

public class BaseActivity extends AppCompatActivity {

    /**
     * 界面加载时
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//允许异常抛出，便于调试错误
    }

    /**
     * 顶部标题栏
     *
     * @param title          标题
     * @param showBackButton 是否显示返回按钮
     */
    protected void initTitleBar(String title, boolean showBackButton) {
        //获取界面中的标题栏，设置标题
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        titleView.setText(title);
        setSupportActionBar(toolbar);
        //处理返回按钮的显示
        if (!showBackButton) return;
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }
        //返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
