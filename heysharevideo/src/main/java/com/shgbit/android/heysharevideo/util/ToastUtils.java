package com.shgbit.android.heysharevideo.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils {
    private static Toast toast;

    private static void getToast(Context context) {
        if (toast == null) {
            toast = new Toast(context);
        }
    }

    public static void showShortToast(Context context, String msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(Context context, String msg, int duration) {
        try {
            getToast(context);
            toast.setText(msg);
            toast.setDuration(duration);
//            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {

        }
    }
}
