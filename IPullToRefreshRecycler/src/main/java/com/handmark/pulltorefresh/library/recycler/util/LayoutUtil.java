package com.handmark.pulltorefresh.library.recycler.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Display;

import java.lang.reflect.Field;

public final class LayoutUtil {
    public LayoutUtil() {
    }

    public static int GetPixelByDIP(Context context, float dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5F);
    }

    public static int GetPixelBySP(Context context, int sp) {
        return (int) (context.getResources().getDisplayMetrics().scaledDensity * (float) sp);
    }

    public static void unLockScreenRotation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public static void setScreenRotation(Activity activity, boolean isLand) {
        if (isLand) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @SuppressWarnings("deprecation")
    public static void lockScreenRotaion(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        activity.setRequestedOrientation(display.getWidth() > display.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static int getStatusBarHeight(Context context) {
        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            LogUtils.e("get status bar height fail", e);
            return -1;
        }
    }

}
