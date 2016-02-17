package com.skyrimcloud.app.easyscreenshot.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.NotificationCompat;

import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.ui.base.BasePreferenceFragment;
import com.skyrimcloud.app.easyscreenshot.ui.base.BaseToolbarActivity;
import com.skyrimcloud.app.easyscreenshot.ui.setting.SettingCommonActivity;
import com.skyrimcloud.app.easyscreenshot.ui.setting.SettingNotificationbarFunctionActivity;
import com.skyrimcloud.app.easyscreenshot.ui.setting.SettingShakeFunctionActivity;
import com.skyrimcloud.app.easyscreenshot.utils.app.SettingHelper;

import butterknife.ButterKnife;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/16 15:02
 */
public class SettingActivity extends BaseToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
        }

    }


    public class SettingsFragment extends BasePreferenceFragment implements
            Preference.OnPreferenceClickListener {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.default_layout_preferences);
            initPreferences();
        }

        private void initPreferences() {
            {

                Preference preference = findPreference(getString(R.string.setting_about_notification_click_item_preference_key));
                preference.setOnPreferenceClickListener(this);
            }

            {
                Preference preference = findPreference(getString(R.string.setting_about_shake_click_item_preference_key));
                preference.setOnPreferenceClickListener(this);
                preference.setSummary(SettingHelper.isShakeScreenshotFunctionOpen() ?
                        getString(R.string.shake_function_on)
                        : getString(R.string.shake_function_off));
            }
            {
                //设置"输出目录"的summary
                Preference preference = findPreference(getString(R.string.output_folder_preference));
                preference.setSummary(SettingHelper.getOutputFoler().toString());
                preference.setEnabled(false);//表示不能修改
            }

            {
                //设置"版本号"的summary
                Preference preference = findPreference(getString(R.string.app_version_preference));
                preference.setSummary(SettingHelper.getVersionString());
            }
            {
                Preference preference = findPreference(getString(R.string.help_preference));
                preference.setOnPreferenceClickListener(this);
            }

            {
                //常用设置
                Preference preference = findPreference(getString(R.string.setting_common_click_item_preference_key));
                preference.setOnPreferenceClickListener(this);
            }
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            String prefeKey = preference.getKey();

            if (prefeKey.equals(getString(R.string.setting_common_click_item_preference_key))) {

                Intent intent = new Intent(getContext(), SettingCommonActivity.class);
                getContext().startActivity(intent);

            }
            else if (prefeKey.equals(getString(R.string.setting_about_notification_click_item_preference_key))) {
                Intent intent = new Intent(getContext(), SettingNotificationbarFunctionActivity.class);
                getContext().startActivity(intent);
            } else if (prefeKey.equals(getString(R.string.setting_about_shake_click_item_preference_key))) {
                Intent intent = new Intent(getContext(), SettingShakeFunctionActivity.class);
                getContext().startActivity(intent);
            }  else if (prefeKey.equals(getString(R.string.help_preference))) {
                 /*HelpDialog helpDialog=new HelpDialog(getContext());
                    helpDialog.show(getString(R.string.help).toUpperCase(), R.array.help_array_strings);*/
                Intent intent = new Intent(getContext(), HelpActivity.class);
                getContext().startActivity(intent);

            }
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            //现在仅需监听一个项的变化，变化后刷新界面的显示
            if (key.equals(getString(R.string.shake_function_preference))) {
                initPreferences();
            }
        }
    }


}
