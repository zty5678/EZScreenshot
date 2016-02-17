package com.skyrimcloud.app.easyscreenshot.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.ui.base.BaseBackableToolbarActivity;
import com.skyrimcloud.app.easyscreenshot.ui.base.BasePreferenceFragment;
import com.skyrimcloud.app.easyscreenshot.utils.app.SettingHelper;

public class SettingShakeFunctionActivity extends BaseBackableToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_shake_function);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.container, new PrefShakeFunctionFragment()).commit();
        }
    }

    public class PrefShakeFunctionFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_about_shake_screenshot);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.shake_function_preference))) {
                SettingHelper.onShakeFunctionSettingChanged();
            }else if (key.equals(getString(R.string.shake_sensitivity_preference))){
                SettingHelper.onShakeSpeedThresholdChanged();
            }

        }
    }

}
