package com.skyrimcloud.app.easyscreenshot.utils.app;

import android.os.Environment;

import com.skyrimcloud.app.easyscreenshot.App;
import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.bean.BusEvents;
import com.skyrimcloud.app.easyscreenshot.utils.BusProvider;
import com.skyrimcloud.app.easyscreenshot.utils.PreferenceUtils;
import com.skyrimcloud.app.easyscreenshot.utils.Utils;

import java.io.File;

public class SettingHelper  {
    public static String getString(int id){
        return App.getContext().getString(id);
    }
    public static boolean getBoolean(int id){
        return App.getContext().getResources().getBoolean(id);
    }
    public static int getInteger(int id){
        return App.getContext().getResources().getInteger(id);
    }

    /**
     * 返回不同的自增的数字，用于显示不同的notification
     * @return
     */
    public static synchronized int getAutoIncrementNotifyId(int beginValue) {
        String key="prefe_key_auto_increment_notification_id";
        int value=PreferenceUtils.getIntFromDefaultPreference(key, beginValue);
        int newVal=value+1;
        PreferenceUtils.saveIntToDefaultPreference(key,newVal);
        return value;
    }
    /**
     * 是否开启了摇一摇
     * @return
     */
    public static boolean isShakeScreenshotFunctionOpen() {
        String key= getString(R.string.shake_function_preference);
        boolean defaultValue= getBoolean(R.bool.shake_function_preference_default_value);
        return PreferenceUtils.getBooleanFromDefaultPreference(key, defaultValue);
    }


    /**
     * 开始截屏时是否振动
     * @return
     */
    public static boolean isVibratorOpenWhenBeginScreenshot() {
        String key= getString(R.string.vibrate_when_screenshot_preference);
        boolean defaultValue= getBoolean(R.bool.vibrate_when_screenshot_preference_default_value);
        return PreferenceUtils.getBooleanFromDefaultPreference(key, defaultValue);
    }

    /**
     * 是否显示通知在状态栏的图标
     * @return
     */
    public static boolean shouldHideStatusbarIcon(){
        String key= getString(R.string.hide_statusbar_icon_preference);
        boolean defaultValue= getBoolean(R.bool.hide_statusbar_icon_preference_default_value);
        return PreferenceUtils.getBooleanFromDefaultPreference(key, defaultValue);
    }

    public static boolean hideSystemDialogs(){
        String key= getString(R.string.auto_close_notificationdrawer_preference);
        boolean defaultValue= getBoolean(R.bool.auto_close_notificationdrawer_preference_default_value);
        return PreferenceUtils.getBooleanFromDefaultPreference(key, defaultValue);
    }
    public static int getDelayOfCloseSystemDialog(){
        String key=getString(R.string.close_notificationdrawer_delay_preference);
        String defaultValueStr= getString(R.string.close_notificationdrawer_delay_preference_default_value);
        defaultValueStr= PreferenceUtils.getStringFromSharedPreferences(key, defaultValueStr);
        int defaultValue=Integer.parseInt(defaultValueStr);
        return defaultValue;
    }
    /**
     * 暂停服务
     */
    public static void pauseScreenshotService() {
        String key= getString(R.string.screenshot_service_paused_or_not_preference);
        PreferenceUtils.saveBooleanToDefaultPreference(key,true);
    }
    public static void resumeScreenshotService() {
        String key= getString(R.string.screenshot_service_paused_or_not_preference);
        PreferenceUtils.saveBooleanToDefaultPreference(key,false);
    }
    public static boolean isScreenshotServicePausing() {
        String key= getString(R.string.screenshot_service_paused_or_not_preference);
        return PreferenceUtils.getBooleanFromDefaultPreference(key,false);
    }


    public static void recordServicePauseStateWhenScreenOff(boolean servicePauseState){
        String key= getString(R.string.screenshot_service_pause_state_before_screen_off_preference);
        PreferenceUtils.saveBooleanToDefaultPreference(key,servicePauseState);
    }
    public static boolean isServicePausingBeforeScreenOff(){
        String key= getString(R.string.screenshot_service_pause_state_before_screen_off_preference);
        return PreferenceUtils.getBooleanFromDefaultPreference(key,false);
    }

    public static ShakeEventListener.ShakeSensitivity getShakeSensitivity(){
        String key=getString(R.string.shake_sensitivity_preference);
        String defaultValueStr= getString(R.string.shake_sensitivity_preference_default_value);
        defaultValueStr= PreferenceUtils.getStringFromSharedPreferences(key, defaultValueStr);
        ShakeEventListener.ShakeSensitivity sensitivity=ShakeEventListener.ShakeSensitivity.parseString(defaultValueStr);
        return sensitivity;
    }

    /**
     * 输出目录
     * @return
     */
    public static File getOutputFoler() {
        String folderName= getString(R.string.output_folder_name);
        File folder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),folderName);
        if (folder.exists() == false || folder.isDirectory() == false) {
            folder.mkdirs();
        }
        return folder;
    }

    /**
     * 输出的图片的文件名
     * @return
     */
    public static String getOutputScreenshotFileName() {
        return getString(R.string.output_file_name) + "-" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 输出的图片质量
     * @return
     */
    public static int getOutputScreenshotQuality(){
        String key=getString(R.string.output_img_quality_prefence);
        String defaultValueStr= getString(R.string.output_img_quality_preference_default_value);
        defaultValueStr= PreferenceUtils.getStringFromSharedPreferences(key, defaultValueStr);
        int defaultValue=Integer.parseInt(defaultValueStr);
        return defaultValue;
    }
    public static String getVersionString() {
        String versionName= Utils.getVersionName(App.getContext());
        return versionName!=null? ("V"+versionName):"null";
    }

    public static boolean shouldStartServiceOnBoot(){
        boolean value=PreferenceUtils.getBooleanFromDefaultPreference(
                getString(R.string.start_service_on_boot_preference),
                getBoolean(R.bool.start_service_on_boot_default_value));
        return value;
    }
    public static boolean showNotificationWhenGeneratedSuccessfully(){
        boolean value=PreferenceUtils.getBooleanFromDefaultPreference(
                getString(R.string.show_success_notification_preference),
                getBoolean(R.bool.show_success_notification_preference_default_value)
        );
        return value;
    }

    /*************************EVENT**********************/

    public static void onHideStatusbarIconSettingChanged() {
        BusProvider.get().post(new BusEvents.SettingChangedEvent(
                BusEvents.SettingEventType.HideStatusbarIconSetting
        ));
    }

    public static void onShakeFunctionSettingChanged() {
        BusProvider.get().post(new BusEvents.SettingChangedEvent(
                BusEvents.SettingEventType.ShakeFunctionSettingChanged));
    }
    public static void onShakeSpeedThresholdChanged(){
        BusProvider.get().post(new BusEvents.SettingChangedEvent(
                BusEvents.SettingEventType.ShakeSpeedThresholdChanged));
    }



}
