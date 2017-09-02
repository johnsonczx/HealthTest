package org.njgroup.health.heart_rate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.njgroup.health.BaseActivity;
import org.njgroup.health.R;

/**
 * 心率测试界面类
 *
 * @author LeiYu
 */
public class HeartRateActivity extends BaseActivity
        implements View.OnClickListener {
    private HeartRateView heartRateView;//心率摄像头视图

    //界面加载时
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载界面
        setContentView(R.layout.activity_heart_rate);
        //设定标题栏
        initTitleBar("心率测试", true);
        //初始化视图
        initView();
    }

    //视图初始化
    private void initView() {
        //开始按钮
        Button btnActionStart = (Button) findViewById(R.id.btn_action_start);
        //曲线图
        HeartRateChart heartRateChart = (HeartRateChart) findViewById(R.id.heart_rate_chart);
        //相机预览
        SurfaceView mPreview = (SurfaceView) findViewById(R.id.sv_preview);
        heartRateView = new HeartRateView(this, mPreview, heartRateChart);
        //按钮监听
        btnActionStart.setOnClickListener(this);
    }

    //根据权限情况继续执行初始化
    private void startWithPermit(boolean hasPermit) {
        if (!hasPermit) return;//没有权限，直接返回
        heartRateView.initCamera();//初始化相机
        heartRateView.reStart();//开始心率检测
    }

    //开始按钮的点击事件
    @Override
    public void onClick(View view) {
        //检查权限，然后根据权限情况继续执行初始化
        startWithPermit(PermitTool.verifyCamera(this));
    }

    //界面销毁时，要释放资源
    @Override
    protected void onDestroy() {
        heartRateView.destroy();//释放资源
        super.onDestroy();
    }

    //按下返回键时，要释放资源
    @Override
    public void onBackPressed() {
        heartRateView.destroy();
        super.onBackPressed();
    }

    //动态授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //判断用户是否授权
        boolean state = PermitTool.isAllGranted(grantResults);
        //如果没有授权，那么给出提示
        if (!state) heartRateView.showSnackBar();
        //根据权限情况继续执行初始化
        startWithPermit(state);
    }

}
