package com.skyrimcloud.app.easyscreenshot.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.ui.base.BaseBackableToolbarActivity;
import com.skyrimcloud.app.easyscreenshot.ui.base.BasePreferenceFragment;

public class SettingCommonActivity extends BaseBackableToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_common);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.container, new SettingsCommonFragment()).commit();
        }


    }

    public static class SettingsCommonFragment  extends BasePreferenceFragment implements
            Preference.OnPreferenceClickListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference_common);
            initPreferences();
        }

        private void initPreferences() {
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }

}
