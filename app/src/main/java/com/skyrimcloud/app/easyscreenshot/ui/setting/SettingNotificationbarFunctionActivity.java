package com.skyrimcloud.app.easyscreenshot.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.ui.base.BaseBackableToolbarActivity;
import com.skyrimcloud.app.easyscreenshot.ui.base.BasePreferenceFragment;
import com.skyrimcloud.app.easyscreenshot.utils.app.SettingHelper;

public class SettingNotificationbarFunctionActivity extends BaseBackableToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notificationbar_function);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.container, new PrefNotificationbarFunctionFragment()).commit();
        }
    }

    public class PrefNotificationbarFunctionFragment extends BasePreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_about_notification_screenshot);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.hide_statusbar_icon_preference))){
                SettingHelper.onHideStatusbarIconSettingChanged();
            }

        }

    }

}
