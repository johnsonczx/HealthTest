package org.njgroup.health.heart_rate;

import android.content.Context;
import android.widget.Toast;


/**
 * 提示显示工具
 *
 * @author LeiYu
 */

public class ToastUtil {
    private static Toast sToast = null;//要显示的Toast

    //显示Toast
    public static void showToast(Context context, String msg) {
        if (sToast == null) {//Toast为空，则重新初始化
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {//否则，进行复用
            sToast.setText(msg);
        }
        //显示
        sToast.show();
    }

    // 主要针对需要在某个时候，取消提示
    public static void cancelToast() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }
}
