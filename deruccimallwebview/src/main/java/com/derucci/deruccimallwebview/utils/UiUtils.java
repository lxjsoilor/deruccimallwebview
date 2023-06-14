package com.derucci.deruccimallwebview.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

public class UiUtils {
    // 获取状态栏高度
    static public int getStatusBarHeight(Context context) {
        int result = 0;
        @SuppressLint("InternalInsetResource") int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // dp转像素
    static public int dpToPx(Activity activity, int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    // 设置状态栏文字黑色
    static public void setNavigationBarBlack(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            int newUiOptions = decorView.getSystemUiVisibility();
            newUiOptions |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(newUiOptions);
        }
    }

    // 设置状态栏文字白色
    static public void setNavigationBarWhite(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
            int flags = decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decor.setSystemUiVisibility(flags);
        }
    }

    // 获取键盘高度
    public static int getSoftKeyboardHeight(Activity activity) {
        // 获取当前 Activity 的根视图
        View rootView = activity.getWindow().getDecorView().getRootView();
        // 获取当前 Activity 的可见高度
        int visibleHeight = rootView.getHeight();
        // 获取当前 Activity 的不可见高度
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        int invisibleHeight = rect.bottom;
        // 计算软键盘高度
        int keyboardHeight = visibleHeight - invisibleHeight;
        // 当软键盘高度为负数时，可能由于输入法切换导致计算不准确，这里将其设为0
        if (keyboardHeight < 0) {
            keyboardHeight = 0;
        }
        return keyboardHeight;
    }

    // 获取屏幕宽高
    public static int[] getScreenSize(Context context) {
        int[] size = new int[2];
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        size[0] = metrics.widthPixels;
        size[1] = metrics.heightPixels;
        return size;
    }

}
