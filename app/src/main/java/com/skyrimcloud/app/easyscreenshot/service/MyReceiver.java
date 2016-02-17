package com.skyrimcloud.app.easyscreenshot.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.skyrimcloud.app.easyscreenshot.utils.app.SettingHelper;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED: {
                if (SettingHelper.shouldStartServiceOnBoot()){
                    MyService.startService(context);
                }
                break;
            }
        }
    }

}