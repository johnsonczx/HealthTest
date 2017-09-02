package org.njgroup.health.heart_rate;

import android.hardware.Camera;

import java.util.LinkedList;

/**
 * 心率工具类
 *
 * @author LeiYu
 */

@SuppressWarnings("deprecation")
public class HeartRateTool {

    /**
     * 添加新的心率点到心率数组
     */
    public static void addToHeartList(int point, LinkedList<Integer> list) {
        if (list.peekLast() == null || list.peekLast() != point) {
            list.add(point);
        }
    }

    /**
     * 获取相机预览的最小尺寸
     */
    public static Camera.Size getSmallestPreviewSize(int width, int height,
                                                     Camera.Parameters parameters) {
        Camera.Size result = null;
        //获取支持的尺寸
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            //类似选择排序第一步，来选取那个最小的尺寸
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }

    /**
     * 源自网络：判断点是否为错误的心率统计点
     */
    public static boolean isErrorPoint(int imgAvg) {
        return imgAvg == 0 || imgAvg == 255 || imgAvg < 150;
    }

    /**
     * 源自网络：心率估算算法
     *
     * @param averageData 红色分量数组
     * @return 心率
     */
    public static int processData(LinkedList<Integer> averageData) {
        int dInt = 0;
        int count = 0;
        boolean isRise = false;
        //遍历每一个分量值
        for (Integer integer : averageData) {
            if (dInt == 0) {
                dInt = integer;
                continue;
            }
            //如果大于阈值，就认为是一次心跳
            if (integer > dInt) {
                if (!isRise) {
                    count++;
                    isRise = true;
                }
            } else {
                isRise = false;
            }
            dInt = integer;
        }
        return count;
    }

    /**
     * 源自网络：通过计算图片中的红色分量，估算心率点参考值
     */
    public static int getImageHeartRefer(byte[] yuv420sp, Camera.Size size) {
        if (yuv420sp == null) return 0;
        int frameSize = size.width * size.height;
        int sum = decodeYUV420SPtoRedSum(yuv420sp, size.width, size.height);
        return (sum / frameSize);
    }

    /**
     * 源自网络：计算图片中的红色分量值
     */
    private static int decodeYUV420SPtoRedSum(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null) return 0;
        int frameSize = width * height;
        int sum = 0;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & yuv420sp[yp]) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                int red = (pixel >> 16) & 0xff;
                sum += red;
            }
        }
        return sum;
    }
}
