package com.skyrimcloud.app.easyscreenshot.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/13 14:33
 */
public class Utils {

    /**
     * 震动
     *
     * @param context
     * @param milliseconds
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    public static void vibrateGently(Context context) {
        vibrate(context, 50);
    }

    public static void gotoActivity(Context context, Class clazz) {
        context.startActivity(new Intent(context, clazz));
    }

    /**
     * 获取应用的versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Point getScreenSize(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;
        return new Point(width, height);
    }

    /**
     * 判断屏幕是否点亮
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager powermanager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powermanager.isInteractive();
    }

    /**
     * 根据手机的分辨率 将 dp转成px
     *
     * @param context
     * @param dpValue dp值
     * @return px值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Check if any apps are installed on the app to receive this intent.
     */
    public static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 将一个异常转化成字符串
     * @param throwable
     * @return
     */
    public static String getStackTrace(final Throwable throwable) {
        if (throwable!=null){
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }
        return null;
    }
}
