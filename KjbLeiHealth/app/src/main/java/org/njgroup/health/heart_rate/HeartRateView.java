package org.njgroup.health.heart_rate;

import android.app.Activity;
import android.hardware.Camera;
import android.support.design.widget.Snackbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.njgroup.health.R;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 心率摄像头视图
 *
 * @author LeiYu
 */

@SuppressWarnings("deprecation")
public class HeartRateView implements Camera.PreviewCallback,
        SurfaceHolder.Callback {
    private Activity activity;//界面所在的Activity
    private AtomicBoolean processing = new AtomicBoolean(true);//用于控制同步
    private LinkedList<Integer> averageData = new LinkedList<>();//心率数据记录列表
    private long endTime = 0;//心率终止时间计时
    private int drawCount = 0;

    private SurfaceView mPreview;//相机预览视图
    private SurfaceHolder mPreviewHolder;//相机预览视图控制器
    private Camera mCamera = null;//相机
    private Camera.Parameters parameters;//相机参数
    private HeartRateChart heartRateChart;//心率曲线图
    private Snackbar mSnackbar;//授权失败提示条
    private HeartRateTip mTip;//用户信息提示条

    //构造器，传入相关组件
    public HeartRateView(Activity activity, SurfaceView mPreview,
                         HeartRateChart heartRateChart) {
        this.activity = activity;
        this.mPreview = mPreview;
        this.heartRateChart = heartRateChart;
        mTip = new HeartRateTip(activity);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //上次计算尚未完成，或者未开始，直接返回进行等待
        if (!processing.compareAndSet(false, true)) return;
        //获取单次心跳参考值
        int imgAvg = HeartRateTool.getImageHeartRefer(data.clone(),
                camera.getParameters().getPreviewSize());
        //心率列表非空（说明已经开始计算了），并且如果是错误值，说明用户没有用手盖住摄像头
        if (averageData.size() > 0 && HeartRateTool.isErrorPoint(imgAvg)) {
            ToastUtil.showToast(activity, "请用手指盖住摄像头");//给予提示
            reStart();//重新开始心率计算
            return;
        }
        //值正确，则添加到心率统计数组
        HeartRateTool.addToHeartList(imgAvg, averageData);
        //绘制心率曲线
        int nowCount = HeartRateTool.processData(averageData);
        if (drawCount < nowCount) {
            drawCount++;
            heartRateChart.showHeartLine(imgAvg, averageData);
        }
        //到达截止时间后，则停止检测
        if (System.currentTimeMillis() >= endTime) {
            enableFlashLight(false);//关闭闪光灯
            showHeartRate();//提醒用户心率数
            return;//此处return会跳过同步变量重置，因此将会停止心率检测
        }
        //重置同步变量，允许下一次统计
        processing.set(false);
    }

    private void showHeartRate() {
        int tenSecCount = HeartRateTool.processData(averageData);
        String text = "十秒心脏跳动：" + tenSecCount + "次\n"
                + "心率：" + tenSecCount * 6 + "次/分钟";
        mTip.show(text);
    }

    //UI更新时
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera == null) return;
        //获取最小相机预览尺寸
        Camera.Size size = HeartRateTool.getSmallestPreviewSize(width, height, parameters);
        //将尺寸指定给相机参数
        if (size != null) parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);
        //开启相机预览
        mCamera.startPreview();
    }

    //UI创建时
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    //UI销毁时
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    //开启&重新开启心率检测
    public void reStart() {
        drawCount = 0;
        endTime = System.currentTimeMillis() + 10000;//重置时间
        averageData.clear();//重置数据记录列表
        heartRateChart.clear();//重置心率曲线图
        processing.set(false); //开启检测
        mTip.hide();
    }

    //初始化摄像头
    public void initCamera() {
        //已经初始化，则只要打开闪光灯即可
        if (mCamera != null) {
            enableFlashLight(true);
            return;
        }
        //获取并设置预览控制器
        mPreviewHolder = mPreview.getHolder();
        mPreviewHolder.addCallback(this);
        mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //开启摄像头
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            mTip.show(null);
            showSnackBar();//权限不够，给予提示
            mCamera = null;
            return;
        }
        mCamera.setDisplayOrientation(90);
        //打开闪光灯
        enableFlashLight(true);
        //设定摄像头预览
        try {
            mCamera.setPreviewDisplay(mPreviewHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(this);
        //开始显示预览
        mCamera.startPreview();
    }

    //是否开启闪光灯
    private void enableFlashLight(boolean enable) {
        parameters = mCamera.getParameters();
        String mode = enable ? Camera.Parameters.FLASH_MODE_TORCH
                : Camera.Parameters.FLASH_MODE_OFF;
        parameters.setFlashMode(mode);
        mCamera.setParameters(parameters);
    }

    //销毁：资源释放
    public void destroy() {
        //设置停止参数
        processing.set(true);
        ToastUtil.cancelToast();
        //摄像头如果已经初始化，则释放相机资源
        if (mCamera != null) {
            //关闭闪光灯
            enableFlashLight(false);
            //停止摄像头预览
            try {
                mCamera.setPreviewDisplay(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        //提示条如果初始化，则释放
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    //显示提示条。用户拒绝授权后，显示此提示
    public void showSnackBar() {
        //创建提示
        mSnackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                R.string.permission_camera_never_ask_again,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PermitTool.showAppDetailSetting(activity);//监听：点击打开设置
                    }
                });
        //显示提示
        mSnackbar.show();
    }
}
