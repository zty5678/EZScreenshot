package com.skyrimcloud.app.easyscreenshot.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.skyrimcloud.app.easyscreenshot.App;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/16 14:23
 */
public class PreferenceUtils {
    /******************
     * default preferece
     ***************/
    // 默认的SharedPreferences的名字,具体表现在
    private final static String DEFAULT_SHARED_PREFERENCE_NAME = "default";
    public static String getSharedPreferencesName(){
        return DEFAULT_SHARED_PREFERENCE_NAME;
    }

    /**
     * 获得默认的SharedPreferences对象
     *
     * @return
     */
    private static SharedPreferences getMyDefaultPreference() {
        return App.getContext().getSharedPreferences(DEFAULT_SHARED_PREFERENCE_NAME, 0);
    }

    /**
     * 向默认的SharedPreferences中保存一个int值
     *
     * @param key     键
     * @param value   值
     */
    public static void saveIntToDefaultPreference(String key,
                                                  int value) {
        SharedPreferences sharedPreferences = getMyDefaultPreference();

        SharedPreferences.Editor sharedata = sharedPreferences.edit();
        sharedata.putInt(key, value);
        sharedata.commit();
    }

    /**
     * 从默认的SharedPreferences获得一个可能已保存的int值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public static int getIntFromDefaultPreference(String key,
                                                  int defaultValue) {
        SharedPreferences sharedata = getMyDefaultPreference();
        return sharedata.getInt(key, defaultValue);
    }

    /**
     * 向默认的SharedPreferences中保存一个boolean值
     *
     * @param key   键
     * @param value 值
     */
    public static void saveBooleanToDefaultPreference(String key,
                                                      boolean value) {
        SharedPreferences sharedPreferences = getMyDefaultPreference();

        SharedPreferences.Editor sharedata = sharedPreferences.edit();
        sharedata.putBoolean(key, value);
        sharedata.commit();
    }

    /**
     * 从默认的SharedPreferences获得一个可能已保存的boolean值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    public static boolean getBooleanFromDefaultPreference(String key,
                                                          boolean defaultValue) {
        SharedPreferences sharedata = getMyDefaultPreference();
        return sharedata.getBoolean(key, defaultValue);
    }

    /**
     * 向默认的SharedPreferences中保存一个string值
     *
     * @param key   键
     * @param value 值
     */
    public static void saveStringToDefaultPreference(Context context,
                                                     String key, String value) {
        SharedPreferences sharedPreferences = getMyDefaultPreference();
        SharedPreferences.Editor sharedata = sharedPreferences.edit();
        sharedata.putString(key, value);
        sharedata.commit();
    }

    /**
     * 从默认的SharedPreferences中获得一个string值
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public static String getStringFromSharedPreferences(String key, String defValue) {
        SharedPreferences sharedata = getMyDefaultPreference();
        return sharedata.getString(key, defValue);
    }
}
