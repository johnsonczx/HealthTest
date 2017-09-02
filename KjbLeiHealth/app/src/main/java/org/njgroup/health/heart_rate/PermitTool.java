package org.njgroup.health.heart_rate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

/**
 * 用户权限工具类
 *
 * @author LeiYu
 */

public class PermitTool {
    /**
     * 判断并申请摄像头权限：for heart rate
     *
     * @return 现在是否具有该权限
     */
    public static boolean verifyCamera(Activity activity) {
        return verifyPermit(activity, Manifest.permission.CAMERA);
    }

    //判断并申请存储卡权限：for breath speed
    public static boolean verifySdcard(Activity activity) {
        return verifyPermit(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //判断并申请录音权限：for breath speed
    public static boolean verifyRecord(Activity activity) {
        return verifyPermit(activity, Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 判断并申请 某项权限
     *
     * @return 现在是否具有该权限
     */
    private static boolean verifyPermit(Activity activity, String permit) {
        //检查是否有该权限
        int permission = ActivityCompat.checkSelfPermission(activity, permit);
        boolean state = permission == PackageManager.PERMISSION_GRANTED;
        //如果没有，就去申请
        if (!state) {
            ActivityCompat.requestPermissions(activity, new String[]{permit}, 1);
        }
        //立即返回当前的权限状态
        return state;
    }

    /**
     * 是否所有申请都被授权
     */
    public static boolean isAllGranted(int[] grantResults) {
        boolean isAllGranted = true;
        //通过循环遍历所有授权状态
        for (int grant : grantResults) {
            //是否被授权
            if (grant != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    /**
     * 跳转到当前应用设置界面
     */
    public static void showAppDetailSetting(Context context) {
        Intent localIntent = new Intent();
        //配置信息，该信息将指向系统设置
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        //打开设置
        context.startActivity(localIntent);
    }
}
