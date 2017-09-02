package org.njgroup.health.heart_rate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * 心率曲线图
 *
 * @author LeiYu
 */

public class HeartRateChart extends View {
    private int mStrokeColor = Color.RED;
    private float mStrokeWidth = 8;
    private float pathSpace = 10f; //每个路径点的间距
    private long maxX; //x坐标能在屏幕中看的的最大值
    private Paint mPaint;
    private LinkedList<Float> linkedPathList;
    private float minPathY = 0; //y轴最小的点

    //构造器：初始化数据
    public HeartRateChart(Context context) {
        super(context);
        init();
    }

    //构造器：初始化数据
    public HeartRateChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 数据初始化
     */
    private void init() {
        //用于存储曲线的列表
        linkedPathList = new LinkedList<>();
        maxX = getContext().getResources().getDisplayMetrics().widthPixels;
        //绘图画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//反锯齿
        mPaint.setStyle(Paint.Style.STROKE);//模式
        mPaint.setColor(mStrokeColor);//颜色
        mPaint.setStrokeWidth(mStrokeWidth);//粗细
        mPaint.setPathEffect(new CornerPathEffect(60));//效果
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //列表为空，不必绘制
        if (linkedPathList.isEmpty()) {
            return;
        }
        //否则，根据列表中的点，绘制曲线
        Path path = new Path();
        int i = 0;
        for (Float aFloat : linkedPathList) {
            if (i == 0) {
                path.moveTo(i * pathSpace, aFloat);
            }
            path.lineTo(i * pathSpace, aFloat);
            i++;
        }
        canvas.drawPath(path, mPaint);
    }

    //通过不断调用该方法，将传入的参数绘制成一条线
    public void lineTo(float y) {
        linkedPathList.add(y);
        if (linkedPathList.size() * pathSpace > maxX) {//如果线条总长大于屏幕宽度了
            if (minPathY == 0) {
                for (Float aFloat : linkedPathList) {
                    if (minPathY == 0 || minPathY > aFloat) {
                        minPathY = aFloat; //取出链表中Y坐标最小的点，赋值给minPathY
                    }
                }
            }
            linkedPathList.removeFirst();//删掉最右边的点
        }
        invalidate();
    }

    //绘制心率曲线，通过平均值消除偏差
    public void showHeartLine(float point, List<Integer> refer) {
        //点数过少，无法参考平均值，直接绘图
        if (refer == null || refer.size() < 2) {
            lineTo(point);
            return;
        }
        //计算平均值
        int sum = 0;
        for (int i : refer) sum += i;
        int avg = sum / refer.size();
        //计算偏差，控制在50-90之间
        int move = (int) (point - avg);
        move = Math.abs(move);
        if (move < 10) move = move * move;
        if (move > 90) move = move % 20 + 70;
        if (move > 0 && move < 50) move = move % 20 + 50;
        //计算峰值
        int peak = (int) (avg + move + 30 * Math.random());
        peak = peak % 255;
        peak = Math.abs(peak);
        int peak_center = (peak + avg) / 2;
        int peak_oppo = (peak > avg) ? (avg * 2 - peak)
                : (avg + 50 + (peak * peak) % (255 - avg));
        peak_oppo += Math.random() * 40 - 20;
        //绘图
        lineTo(avg);
        lineTo(avg);
        lineTo(peak_oppo);
        lineTo(avg + (int) (Math.random() * 40 - 20));
        lineTo(peak_center + (int) (Math.random() * 100 - 50));
        lineTo(peak);
        lineTo(peak_center + (int) (Math.random() * 100 - 50));
        lineTo(avg);
        lineTo(avg);
        lineTo(avg);
    }

    //绘图清空
    public void clear() {
        linkedPathList.clear();
        minPathY = 0;
        invalidate();
    }
}
