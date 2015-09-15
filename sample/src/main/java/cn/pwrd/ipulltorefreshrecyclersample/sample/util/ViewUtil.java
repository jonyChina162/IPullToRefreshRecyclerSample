package cn.pwrd.ipulltorefreshrecyclersample.sample.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

public class ViewUtil {
    @TargetApi(21)
    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        if (OsVersionUtils.hasLollipop()) {
            return context.getDrawable(resId);
        } else {
            return context.getResources().getDrawable(resId);
        }
    }
}
