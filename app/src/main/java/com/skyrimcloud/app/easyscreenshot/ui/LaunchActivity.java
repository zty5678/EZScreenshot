package com.skyrimcloud.app.easyscreenshot.ui;

import android.os.Bundle;

import com.skyrimcloud.app.easyscreenshot.service.MyService;
import com.skyrimcloud.app.easyscreenshot.ui.base.BaseActivity;
import com.skyrimcloud.app.easyscreenshot.utils.Utils;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/16 15:26
 */
public class LaunchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyService.startService(this);


        Utils.gotoActivity(this, SettingActivity.class);
        closeActivity();


    }

}
